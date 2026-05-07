package com.sau.gym.admin.agent.service.impl;

import com.sau.gym.admin.agent.model.AgentEntityCandidate;
import com.sau.gym.admin.agent.model.AgentEntityResolveResult;
import com.sau.gym.admin.agent.model.AgentNlpResult;
import com.sau.gym.admin.agent.nlp.AgentTextNormalizer;
import com.sau.gym.admin.agent.nlp.FuzzyMatchUtils;
import com.sau.gym.admin.agent.service.AgentEntityResolveService;
import com.sau.gym.admin.agent.service.AgentNlpService;
import com.sau.gym.admin.mapper.AgentEntityCandidateMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 作者:hfj
 * 功能:Agent业务实体解析服务实现类
 * 功能：
 * 1. 使用 HanLP 分析用户输入
 * 2. 从 Mapper 查询场馆 / 场地候选
 * 3. 对候选进行打分
 * 4. 判断是否命中唯一场馆 / 场地
 * 5. 判断是否存在歧义
 * 日期: 2026/5/7 14:28
 */
@Service
public class AgentEntityResolveServiceImpl implements AgentEntityResolveService {

    /**
     * 高置信度阈值。
     */
    private static final int HIGH_CONFIDENCE_SCORE = 80;

    /**
     * 最低有效分。
     */
    private static final int MIN_VALID_SCORE = 60;

    /**
     * 歧义判断分差。
     *
     * 第一名和第二名差距小于这个值，认为可能存在歧义。
     */
    private static final int AMBIGUOUS_GAP = 10;

    private final AgentEntityCandidateMapper candidateMapper;
    private final AgentNlpService agentNlpService;
    private final AgentTextNormalizer textNormalizer;

    public AgentEntityResolveServiceImpl(AgentEntityCandidateMapper candidateMapper,
                                         AgentNlpService agentNlpService,
                                         AgentTextNormalizer textNormalizer) {
        this.candidateMapper = candidateMapper;
        this.agentNlpService = agentNlpService;
        this.textNormalizer = textNormalizer;
    }

    /**
     * 解析用户输入中的场馆、场地等业务实体。
     */
    @Override
    public AgentEntityResolveResult resolve(String userText) {
        if (userText == null || userText.trim().isEmpty()) {
            return AgentEntityResolveResult.empty();
        }

        /*
         * 第一步：
         * 调用 HanLP NLP 服务。
         */
        AgentNlpResult nlpResult = agentNlpService.analyze(userText);

        if (nlpResult == null || isBlank(nlpResult.getNormalizedText())) {
            return AgentEntityResolveResult.empty();
        }

        /*
         * 第二步：
         * 加载全部候选。
         */
        List<AgentEntityCandidate> candidates = loadAllCandidates();

        if (candidates.isEmpty()) {
            return AgentEntityResolveResult.empty();
        }

        /*
         * 第三步：
         * 分别计算场馆候选分数和场地候选分数。
         */
        List<ScoredCandidate> venueScores = new ArrayList<>();
        List<ScoredCandidate> courtScores = new ArrayList<>();

        for (AgentEntityCandidate candidate : candidates) {
            normalizeCandidate(candidate);

            int score = calculateScore(nlpResult, candidate);
            ScoredCandidate scoredCandidate = new ScoredCandidate(candidate, score);

            if ("VENUE".equals(candidate.getType())) {
                venueScores.add(scoredCandidate);
            } else if ("COURT".equals(candidate.getType())) {
                courtScores.add(scoredCandidate);
            }
        }

        venueScores.sort(Comparator.comparingInt(ScoredCandidate::getScore).reversed());
        courtScores.sort(Comparator.comparingInt(ScoredCandidate::getScore).reversed());

        ScoredCandidate bestVenue = venueScores.isEmpty() ? null : venueScores.get(0);
        ScoredCandidate bestCourt = courtScores.isEmpty() ? null : courtScores.get(0);

        /*
         * 第四步：
         * 构造解析结果。
         */
        AgentEntityResolveResult result = new AgentEntityResolveResult();

        if (bestVenue != null && bestVenue.getScore() >= MIN_VALID_SCORE) {
            AgentEntityCandidate venue = bestVenue.getCandidate();

            result.setVenueId(venue.getVenueId());
            result.setVenueName(venue.getVenueName());
            result.setScore(result.getScore() + bestVenue.getScore());
        }

        if (bestCourt != null && bestCourt.getScore() >= MIN_VALID_SCORE) {
            AgentEntityCandidate court = bestCourt.getCandidate();

            result.setCourtId(court.getCourtId());
            result.setCourtName(court.getCourtName());
            result.setCourtType(court.getCourtType());

            /*
             * 如果只命中了场地，没有单独命中场馆，
             * 就用场地所属场馆补充 venueId。
             */
            if (result.getVenueId() == null) {
                result.setVenueId(court.getVenueId());
                result.setVenueName(court.getVenueName());
            }

            result.setScore(result.getScore() + bestCourt.getScore());
        }

        result.setResolved(result.getVenueId() != null || result.getCourtId() != null);

        /*
         * 第五步：
         * 判断场地是否存在歧义。
         */
        boolean ambiguous = isAmbiguous(courtScores);
        result.setAmbiguous(ambiguous);

        if (ambiguous) {
            result.setCandidates(buildCandidateNames(courtScores));
        }

        return result;
    }

    /**
     * 加载所有候选实体。
     *
     * 候选来源：
     * 1. 场馆真实名称
     * 2. 场馆别名
     * 3. 场地真实名称
     * 4. 场地别名
     */
    private List<AgentEntityCandidate> loadAllCandidates() {
        List<AgentEntityCandidate> result = new ArrayList<>();

        List<AgentEntityCandidate> venueNames = candidateMapper.selectVenueNameCandidates();
        List<AgentEntityCandidate> venueAliases = candidateMapper.selectVenueAliasCandidates();
        List<AgentEntityCandidate> courtNames = candidateMapper.selectCourtNameCandidates();
        List<AgentEntityCandidate> courtAliases = candidateMapper.selectCourtAliasCandidates();

        if (venueNames != null) {
            result.addAll(venueNames);
        }
        if (venueAliases != null) {
            result.addAll(venueAliases);
        }
        if (courtNames != null) {
            result.addAll(courtNames);
        }
        if (courtAliases != null) {
            result.addAll(courtAliases);
        }

        return result;
    }

    /**
     * 标准化候选实体。
     *
     * 真实名称候选没有 normalized_match_name，
     * 这里统一补上。
     */
    private void normalizeCandidate(AgentEntityCandidate candidate) {
        if (candidate == null) {
            return;
        }

        if (isBlank(candidate.getNormalizedMatchName())) {
            candidate.setNormalizedMatchName(
                    textNormalizer.normalize(candidate.getMatchName())
            );
        }
    }

    /**
     * 计算候选实体匹配分数。
     *
     * 打分逻辑：
     * 1. 完整包含命中
     * 2. 候选名称包含用户输入
     * 3. HanLP 分词命中
     * 4. 模糊相似度
     * 5. 场地类型命中
     * 6. 场地编号命中
     */
    private int calculateScore(AgentNlpResult nlpResult, AgentEntityCandidate candidate) {
        if (nlpResult == null || candidate == null) {
            return 0;
        }

        String normalizedText = nlpResult.getNormalizedText();
        String matchName = candidate.getNormalizedMatchName();

        if (isBlank(normalizedText) || isBlank(matchName)) {
            return 0;
        }

        int score = 0;

        /*
         * 1. 完整包含命中。
         *
         * 用户输入：我要预约北门1号篮球场
         * 候选：北门1号篮球场
         */
        if (normalizedText.contains(matchName)) {
            score += Boolean.TRUE.equals(candidate.getAlias()) ? 120 : 100;
        }

        /*
         * 2. 候选名称包含用户输入。
         *
         * 用户只输入短词时可能触发。
         */
        if (matchName.contains(normalizedText)) {
            score += Boolean.TRUE.equals(candidate.getAlias()) ? 80 : 60;
        }

        /*
         * 3. HanLP 分词命中。
         */
        if (nlpResult.getTerms() != null) {
            for (String term : nlpResult.getTerms()) {
                if (isBlank(term)) {
                    continue;
                }

                String normalizedTerm = textNormalizer.normalize(term);

                if (matchName.equals(normalizedTerm)) {
                    score += Boolean.TRUE.equals(candidate.getAlias()) ? 100 : 80;
                } else if (matchName.contains(normalizedTerm) || normalizedTerm.contains(matchName)) {
                    score += 30;
                }
            }
        }

        /*
         * 4. 模糊相似度。
         */
        score += FuzzyMatchUtils.similarity(normalizedText, matchName) / 2;

        /*
         * 5. 场地类型命中。
         */
        if ("COURT".equals(candidate.getType()) && !isBlank(candidate.getCourtType())) {
            String normalizedCourtType = textNormalizer.normalize(candidate.getCourtType());

            if (normalizedText.contains(normalizedCourtType)) {
                score += 40;
            }

            if (nlpResult.getCourtTypeKeywords() != null
                    && nlpResult.getCourtTypeKeywords().contains(normalizedCourtType)) {
                score += 50;
            }
        }

        /*
         * 6. 场地编号命中。
         */
        String candidateNumber = extractCourtNumber(matchName);

        if (candidateNumber != null
                && nlpResult.getCourtNumbers() != null
                && nlpResult.getCourtNumbers().contains(candidateNumber)) {
            score += 30;
        }

        return score;
    }

    /**
     * 判断是否存在歧义。
     *
     * 例子：
     * 用户说“篮球场”，系统里有篮球1号场、篮球2号场。
     * 如果多个候选分数接近，就不要直接选第一个。
     */
    private boolean isAmbiguous(List<ScoredCandidate> scores) {
        if (scores == null || scores.size() < 2) {
            return false;
        }

        ScoredCandidate first = scores.get(0);
        ScoredCandidate second = scores.get(1);

        return first.getScore() >= MIN_VALID_SCORE
                && second.getScore() >= MIN_VALID_SCORE
                && Math.abs(first.getScore() - second.getScore()) < AMBIGUOUS_GAP
                && first.getScore() < HIGH_CONFIDENCE_SCORE;
    }

    /**
     * 构造歧义候选名称。
     */
    private List<String> buildCandidateNames(List<ScoredCandidate> scores) {
        return scores.stream()
                .filter(item -> item.getScore() >= MIN_VALID_SCORE)
                .limit(3)
                .map(item -> item.getCandidate().getCourtName())
                .distinct()
                .toList();
    }

    /**
     * 从文本中提取场地编号。
     */
    private String extractCourtNumber(String text) {
        if (isBlank(text)) {
            return null;
        }

        for (int i = 1; i <= 30; i++) {
            String key = i + "号";
            if (text.contains(key)) {
                return String.valueOf(i);
            }
        }

        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * 带分数的候选实体。
     */
    private static class ScoredCandidate {

        private final AgentEntityCandidate candidate;
        private final int score;

        private ScoredCandidate(AgentEntityCandidate candidate, int score) {
            this.candidate = candidate;
            this.score = score;
        }

        public AgentEntityCandidate getCandidate() {
            return candidate;
        }

        public int getScore() {
            return score;
        }
    }
}
