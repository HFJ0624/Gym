package com.sau.gym.admin.agent.service.impl;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.sau.gym.admin.agent.nlp.AgentTextNormalizer;
import com.sau.gym.admin.agent.service.AgentNlpService;
import com.sau.gym.admin.agent.model.AgentNlpResult;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 作者:hfj
 * 功能:基于HanLP的AgentNLP服务实现
 * 说明：
 * HanLP 在这里不直接决定业务结果。
 * 它只负责对用户输入做分词和关键词提取。
 * 日期: 2026/5/6 21:13
 */
@Service
public class HanlpAgentNlpServiceImpl implements AgentNlpService {

    private final AgentTextNormalizer textNormalizer;

    /**
     * 当前系统支持识别的场地类型关键词。
     *
     * 后续可以改成从数据库 court_type 字段动态加载。
     */
    private static final Set<String> COURT_TYPE_WORDS = new HashSet<>();

    static {
        COURT_TYPE_WORDS.add("篮球场");
        COURT_TYPE_WORDS.add("羽毛球场");
        COURT_TYPE_WORDS.add("足球场");
        COURT_TYPE_WORDS.add("网球场");
        COURT_TYPE_WORDS.add("乒乓球场");
        COURT_TYPE_WORDS.add("游泳馆");
        COURT_TYPE_WORDS.add("健身房");
    }

    public HanlpAgentNlpServiceImpl(AgentTextNormalizer textNormalizer) {
        this.textNormalizer = textNormalizer;
    }

    /**
     * 分析用户输入文本。
     */
    @Override
    public AgentNlpResult analyze(String text) {
        AgentNlpResult result = new AgentNlpResult();

        result.setRawText(text);

        String normalizedText = textNormalizer.normalize(text);
        result.setNormalizedText(normalizedText);

        if (normalizedText == null || normalizedText.trim().isEmpty()) {
            return result;
        }

        //HanLP分词。
        List<Term> terms = HanLP.segment(normalizedText);

        for (Term term : terms) {
            if (term == null || term.word == null || term.word.trim().isEmpty()) {
                continue;
            }

            String word = term.word.trim();
            result.getTerms().add(word);

            //识别场地类型。
            if (COURT_TYPE_WORDS.contains(word)) {
                result.getCourtTypeKeywords().add(word);
            }
        }

        //有时 HanLP 不一定会把“篮球场”完整切出来，所以再用 contains 做一层兜底。
        for (String courtType : COURT_TYPE_WORDS) {
            if (normalizedText.contains(courtType)
                    && !result.getCourtTypeKeywords().contains(courtType)) {
                result.getCourtTypeKeywords().add(courtType);
            }
        }

        //提取场地编号。
        extractCourtNumbers(normalizedText, result);

        return result;
    }

    /**
     * 提取场地编号。
     *
     * 例如：
     * 一号篮球场 → 经过标准化后是 1号篮球场
     * 篮球2号场 → 识别出 2
     */
    private void extractCourtNumbers(String normalizedText, AgentNlpResult result) {
        for (int i = 1; i <= 30; i++) {
            String key = i + "号";

            if (normalizedText.contains(key)) {
                result.getCourtNumbers().add(String.valueOf(i));
            }
        }
    }
}
