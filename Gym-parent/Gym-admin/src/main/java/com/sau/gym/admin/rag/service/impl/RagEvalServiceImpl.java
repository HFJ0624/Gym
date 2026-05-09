package com.sau.gym.admin.rag.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sau.gym.admin.mapper.RagEvalMapper;
import com.sau.gym.admin.rag.service.RagEvalSearchService;
import com.sau.gym.admin.rag.service.RagEvalService;
import com.sau.gym.model.dto.rag.RagEvalCaseDto;
import com.sau.gym.model.dto.rag.RagEvalRunDto;
import com.sau.gym.model.entity.rag.RagEvalCase;
import com.sau.gym.model.entity.rag.RagEvalResult;
import com.sau.gym.model.entity.rag.RagEvalRun;
import com.sau.gym.model.entity.rag.RagEvalSearchResult;
import com.sau.gym.model.vo.rag.RagSourceVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 作者:hfj
 * 功能:RAG 评估服务实现
 * 作用：
 * 1. 管理 RAG 评估用例
 * 2. 批量运行 RAG 评估
 * 3. 计算 Top1 命中率、TopK 命中率、来源正确率、无答案率等指标
 * 4. 保存每次评估批次和每条评估明细
 * 日期: 2026/5/9 19:42
 */
@Service
public class RagEvalServiceImpl implements RagEvalService {

    private final RagEvalMapper ragEvalMapper;

    private final RagEvalSearchService ragEvalSearchService;

    public RagEvalServiceImpl(RagEvalMapper ragEvalMapper,
                              RagEvalSearchService ragEvalSearchService) {
        this.ragEvalMapper = ragEvalMapper;
        this.ragEvalSearchService = ragEvalSearchService;
    }

    /**
     * 分页查询 RAG 评估用例。
     *
     * @param current 当前页
     * @param limit 每页数量
     * @param category 分类过滤，例如：预约规则、退款规则、场馆知识
     * @param enabled 是否启用：1启用，0禁用
     * @param keyword 问题关键词搜索
     * @return 分页后的评估用例列表
     */
    @Override
    public PageInfo<RagEvalCase> pageCases(Integer current,
                                           Integer limit,
                                           String category,
                                           Integer enabled,
                                           String keyword) {
        PageHelper.startPage(current, limit);

        //根据分类、启用状态、关键词查询评估用例。
        List<RagEvalCase> list = ragEvalMapper.selectCasePage(category, enabled, keyword);

        return new PageInfo<>(list);
    }

    /**
     * 新增或修改 RAG 评估用例。
     * @param dto 前端传入的评估用例 DTO
     */
    @Override
    public void saveCase(RagEvalCaseDto dto) {

        RagEvalCase evalCase = new RagEvalCase();

        evalCase.setId(dto.getId());
        evalCase.setQuestion(dto.getQuestion());
        evalCase.setExpectedDocIds(dto.getExpectedDocIds());
        evalCase.setExpectedKeywords(dto.getExpectedKeywords());

        //expectedNoAnswer 表示这个问题是否期望系统回答“无答案”。
        //0：这个问题应该能从知识库中回答 1：这个问题不属于知识库范围，期望系统识别为无答案
        evalCase.setExpectedNoAnswer(dto.getExpectedNoAnswer() == null ? 0 : dto.getExpectedNoAnswer());

        evalCase.setCategory(dto.getCategory());

        //enabled 表示该评估用例是否启用。
        evalCase.setEnabled(dto.getEnabled() == null ? 1 : dto.getEnabled());

        evalCase.setRemark(dto.getRemark());

        //根据 ID 判断新增还是修改。
        if (dto.getId() == null) {
            ragEvalMapper.insertCase(evalCase);
        } else {
            ragEvalMapper.updateCase(evalCase);
        }
    }

    /**
     * 删除 RAG 评估用例。
     * @param id 用例 ID
     */
    @Override
    public void deleteCase(Long id) {
        ragEvalMapper.deleteCase(id);
    }

    /**
     * 执行一次 RAG 批量评估。
     * 第一版是同步执行：
     * 前端点击“运行评估”后，请求会一直等待所有用例跑完。
     * 如果后面评估用例数量变多，例如几十条、几百条，
     * 建议改成异步任务：
     * 1. 先创建 RUNNING 批次
     * 2. 后台线程慢慢跑
     * 3. 前端轮询查看结果
     *
     * @param dto 评估运行参数，包括 topK、minScore、category
     * @return 评估批次结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public RagEvalRun runEval(RagEvalRunDto dto) {

        //topK 表示只评估前 K 个命中来源。(默认为3)
        Integer topK = dto.getTopK() == null || dto.getTopK() <= 0 ? 3 : dto.getTopK();

        //minScore 表示最低相似度阈值。(默认0.5)
        BigDecimal minScore = dto.getMinScore() == null ? BigDecimal.valueOf(0.5) : dto.getMinScore();

        //查询所有启用的评估用例。
        List<RagEvalCase> cases = ragEvalMapper.selectEnabledCases(dto.getCategory());

        /*
         * 创建一条评估批次记录。
         *
         * rag_eval_run 表用于保存本次评估的整体指标。
         */
        RagEvalRun run = new RagEvalRun();
        run.setRunNo(generateRunNo());
        run.setTopK(topK);
        run.setMinScore(minScore);
        run.setStatus("RUNNING");

        /*
         * 插入后，MyBatis 会通过 useGeneratedKeys 回填 run.id。
         * 后续每条评估明细都要关联这个 runId。
         */
        ragEvalMapper.insertRun(run);

        /*
         * totalCount：总用例数
         * answerableCount：期望能回答的问题数
         * noAnswerExpectedCount：期望无答案的问题数
         */
        int totalCount = cases.size();
        int answerableCount = 0;
        int noAnswerExpectedCount = 0;

        /*
         * 各类命中计数器。
         */
        int top1HitCount = 0;
        int topkHitCount = 0;
        int sourceCorrectCount = 0;
        int noAnswerCount = 0;
        int noAnswerCorrectCount = 0;

        /*
         * maxScoreSum / maxScoreCount 用于计算平均最高相似度。
         *
         * 只统计 maxScore 不为空的样本。
         */
        BigDecimal maxScoreSum = BigDecimal.ZERO;
        int maxScoreCount = 0;

        /*
         * costSum 用于统计平均耗时。
         */
        long costSum = 0L;

        try {
            /*
             * 遍历每一条评估用例。
             */
            for (RagEvalCase evalCase : cases) {
                /*
                 * 记录当前用例开始时间，用于计算单条耗时。
                 */
                long start = System.currentTimeMillis();

                /*
                 * 创建当前用例的评估明细结果对象。
                 *
                 * rag_eval_result 表中每条记录对应一个问题的一次评估结果。
                 */
                RagEvalResult result = new RagEvalResult();

                result.setRunId(run.getId());
                result.setCaseId(evalCase.getId());
                result.setQuestion(evalCase.getQuestion());
                result.setExpectedDocIds(evalCase.getExpectedDocIds());
                result.setExpectedKeywords(evalCase.getExpectedKeywords());
                result.setExpectedNoAnswer(evalCase.getExpectedNoAnswer());

                try {
                    /*
                     * 判断该用例是否期望无答案。
                     *
                     * expectedNoAnswer = 1：
                     * 表示这个问题不应该从知识库里答出来。
                     *
                     * 例如：
                     * “这个平台能不能预约火箭发射场？”
                     */
                    boolean expectedNoAnswer = Objects.equals(evalCase.getExpectedNoAnswer(), 1);

                    /*
                     * 统计应回答问题数 / 期望无答案问题数。
                     */
                    if (expectedNoAnswer) {
                        noAnswerExpectedCount++;
                    } else {
                        answerableCount++;
                    }

                    /*
                     * 调用 RAG 搜索服务。
                     *
                     * 这里会复用你现有的 RAG 问答逻辑：
                     * 1. 向量检索
                     * 2. 来源召回
                     * 3. 大模型回答
                     */
                    RagEvalSearchResult searchResult = ragEvalSearchService.search(
                            evalCase.getQuestion(),
                            topK,
                            minScore.doubleValue()
                    );

                    /*
                     * 获取 RAG 命中的来源。
                     *
                     * 如果没有来源，统一转为空集合，避免空指针。
                     */
                    List<RagSourceVO> sources = searchResult.getSources() == null
                            ? new ArrayList<>()
                            : searchResult.getSources();

                    /*
                     * 提取实际命中的 docId 列表。
                     *
                     * 后续会保存到 rag_eval_result.retrieved_doc_ids。
                     */
                    List<Long> retrievedDocIds = sources.stream()
                            .map(RagSourceVO::getDocId)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    /*
                     * 获取本次检索的最高相似度。
                     *
                     * 如果 searchResult.getMaxScore() 为空，则 maxScore 也为空。
                     */
                    BigDecimal maxScore = searchResult.getMaxScore() == null
                            ? null
                            : BigDecimal.valueOf(searchResult.getMaxScore());

                    /*
                     * 判断实际是否无答案。
                     *
                     * 当前第一版规则：
                     * 1. 没有任何来源
                     * 2. maxScore 为空
                     * 3. maxScore < minScore
                     *
                     * 满足任意一个条件，就认为 actualNoAnswer = true。
                     */
                    boolean actualNoAnswer = sources.isEmpty()
                            || maxScore == null
                            || maxScore.compareTo(minScore) < 0;

                    /*
                     * 判断无答案预测是否正确。
                     *
                     * 如果期望无答案，并且实际也无答案，则正确。
                     * 如果期望有答案，并且实际有答案，也算正确。
                     */
                    boolean noAnswerCorrect = expectedNoAnswer == actualNoAnswer;

                    /*
                     * 解析期望文档 ID。
                     *
                     * expectedDocIds 示例：
                     * "1,2,3"
                     *
                     * 解析后：
                     * Set<Long> = [1, 2, 3]
                     */
                    Set<Long> expectedDocIdSet = parseLongSet(evalCase.getExpectedDocIds());

                    /*
                     * 解析期望关键词。
                     *
                     * expectedKeywords 示例：
                     * "预约,退款,审核"
                     */
                    Set<String> expectedKeywordSet = parseStringSet(evalCase.getExpectedKeywords());

                    /*
                     * 判断 Top1 是否命中。
                     *
                     * 规则：
                     * RAG 返回的第一个来源 docId 在 expectedDocIds 中。
                     */
                    boolean top1Hit = judgeTop1Hit(sources, expectedDocIdSet);

                    /*
                     * 判断 TopK 是否命中。
                     *
                     * 规则：
                     * RAG 返回的前 K 个来源中，任意一个 docId 在 expectedDocIds 中。
                     */
                    boolean topkHit = judgeTopkHit(sources, expectedDocIdSet);

                    /*
                     * 判断关键词是否命中。
                     *
                     * 当 expectedDocIds 没配置或 docId 不方便维护时，
                     * 用 expectedKeywords 做兜底判断。
                     */
                    boolean keywordHit = judgeKeywordHit(sources, expectedKeywordSet);

                    /*
                     * 判断来源是否正确。
                     *
                     * 对期望无答案的问题：
                     * 只要实际也无答案，就认为来源判断正确。
                     *
                     * 对期望有答案的问题：
                     * TopK 命中 或 关键词命中，就认为来源正确。
                     */
                    boolean sourceCorrect;

                    if (expectedNoAnswer) {
                        sourceCorrect = actualNoAnswer;
                    } else {
                        sourceCorrect = topkHit || keywordHit;
                    }

                    /*
                     * 累加各类指标计数。
                     */
                    if (top1Hit) {
                        top1HitCount++;
                    }

                    if (topkHit) {
                        topkHitCount++;
                    }

                    if (sourceCorrect) {
                        sourceCorrectCount++;
                    }

                    if (actualNoAnswer) {
                        noAnswerCount++;
                    }

                    if (noAnswerCorrect) {
                        noAnswerCorrectCount++;
                    }

                    /*
                     * 累计 maxScore，用于最后计算平均最高相似度。
                     */
                    if (maxScore != null) {
                        maxScoreSum = maxScoreSum.add(maxScore);
                        maxScoreCount++;
                    }

                    /*
                     * 计算当前用例耗时。
                     */
                    long costMs = System.currentTimeMillis() - start;
                    costSum += costMs;

                    /*
                     * 写入当前用例评估结果。
                     */
                    result.setRetrievedDocIds(joinLongs(retrievedDocIds));
                    result.setMatchedSources(JSON.toJSONString(sources));
                    result.setMaxScore(maxScore);
                    result.setTop1Hit(bool(top1Hit));
                    result.setTopkHit(bool(topkHit));
                    result.setSourceCorrect(bool(sourceCorrect));
                    result.setActualNoAnswer(bool(actualNoAnswer));
                    result.setNoAnswerCorrect(bool(noAnswerCorrect));
                    result.setAnswerText(searchResult.getAnswer());
                    result.setCostMs(costMs);

                } catch (Exception e) {
                    /*
                     * 单条用例失败时，不让整个评估批次直接中断。
                     *
                     * 原因：
                     * 评估中可能某一个问题调用模型失败或检索异常，
                     * 但其他问题仍然可以继续评估。
                     */
                    long costMs = System.currentTimeMillis() - start;
                    costSum += costMs;

                    result.setCostMs(costMs);
                    result.setErrorMessage(e.getMessage());

                    /*
                     * 异常样本默认视为未命中。
                     */
                    result.setTop1Hit(0);
                    result.setTopkHit(0);
                    result.setSourceCorrect(0);

                    /*
                     * 异常情况下，当前第一版按 actualNoAnswer = 1 处理。
                     */
                    result.setActualNoAnswer(1);
                    result.setNoAnswerCorrect(0);
                }

                /*
                 * 无论当前用例成功还是失败，都保存一条明细结果。
                 */
                ragEvalMapper.insertResult(result);
            }

            /*
             * 循环结束后，开始汇总整个评估批次指标。
             */
            run.setTotalCount(totalCount);
            run.setAnswerableCount(answerableCount);
            run.setNoAnswerExpectedCount(noAnswerExpectedCount);
            run.setTop1HitCount(top1HitCount);
            run.setTopkHitCount(topkHitCount);
            run.setSourceCorrectCount(sourceCorrectCount);
            run.setNoAnswerCount(noAnswerCount);
            run.setNoAnswerCorrectCount(noAnswerCorrectCount);

            /*
             * 计算百分比指标。
             *
             * rate() 返回的是百分比：
             * 例如 80.0000 表示 80%。
             */
            run.setTop1HitRate(rate(top1HitCount, totalCount));
            run.setTopkHitRate(rate(topkHitCount, totalCount));
            run.setSourceCorrectRate(rate(sourceCorrectCount, totalCount));
            run.setNoAnswerRate(rate(noAnswerCount, totalCount));
            run.setNoAnswerAccuracy(rate(noAnswerCorrectCount, totalCount));

            /*
             * 计算平均最高相似度。
             *
             * 如果没有任何有效 maxScore，则记为 0。
             */
            run.setAvgMaxScore(maxScoreCount == 0
                    ? BigDecimal.ZERO
                    : maxScoreSum.divide(BigDecimal.valueOf(maxScoreCount), 6, RoundingMode.HALF_UP));

            /*
             * 计算平均耗时。
             */
            run.setAvgCostMs(totalCount == 0
                    ? BigDecimal.ZERO
                    : BigDecimal.valueOf(costSum).divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP));

            /*
             * 批次执行成功。
             */
            run.setStatus("SUCCESS");

            /*
             * 更新评估批次结果。
             */
            ragEvalMapper.updateRunResult(run);

            return run;

        } catch (Exception e) {
            /*
             * 这里捕获的是整个批次级别异常。
             *
             * 注意：
             * 单条用例异常在内部已经处理了。
             * 能走到这里，通常是数据库、事务、批次更新等系统级异常。
             */
            run.setStatus("FAILED");
            run.setErrorMessage(e.getMessage());

            ragEvalMapper.updateRunResult(run);

            throw e;
        }
    }

    /**
     * 分页查询评估批次列表。
     */
    @Override
    public PageInfo<RagEvalRun> pageRuns(Integer current, Integer limit) {
        PageHelper.startPage(current, limit);
        List<RagEvalRun> list = ragEvalMapper.selectRunPage();
        return new PageInfo<>(list);
    }

    /**
     * 根据 runId 查询评估批次主记录。
     */
    @Override
    public RagEvalRun getRun(Long runId) {
        return ragEvalMapper.selectRunById(runId);
    }

    /**
     * 根据 runId 查询评估明细结果。
     */
    @Override
    public List<RagEvalResult> getResults(Long runId) {
        return ragEvalMapper.selectResultsByRunId(runId);
    }

    /**
     * 生成评估批次号。
     *
     * 示例：
     * RAG-EVAL-1710000000000
     */
    private String generateRunNo() {
        return "RAG-EVAL-" + System.currentTimeMillis();
    }

    /**
     * boolean 转数据库中的 0 / 1。
     *
     * true  -> 1
     * false -> 0
     */
    private int bool(boolean value) {
        return value ? 1 : 0;
    }

    /**
     * 计算百分比。
     *
     * @param count 命中数量
     * @param total 总数量
     * @return 百分比，例如 80.0000 表示 80%
     */
    private BigDecimal rate(int count, int total) {
        if (total <= 0) {
            return BigDecimal.ZERO;
        }

        return BigDecimal.valueOf(count)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP);
    }

    /**
     * 判断 Top1 是否命中。
     *
     * 命中规则：
     * RAG 返回的第一个来源 docId 在 expectedDocIds 中。
     */
    private boolean judgeTop1Hit(List<RagSourceVO> sources, Set<Long> expectedDocIds) {
        if (sources == null || sources.isEmpty() || expectedDocIds.isEmpty()) {
            return false;
        }

        Long firstDocId = sources.get(0).getDocId();

        return firstDocId != null && expectedDocIds.contains(firstDocId);
    }

    /**
     * 判断 TopK 是否命中。
     *
     * 命中规则：
     * RAG 返回的前 K 个来源里，只要有一个 docId 在 expectedDocIds 中，就算命中。
     *
     * 注意：
     * sources 在 RagEvalSearchServiceImpl 中已经按 topK 截断，
     * 所以这里遍历 sources 就等价于遍历 TopK 结果。
     */
    private boolean judgeTopkHit(List<RagSourceVO> sources, Set<Long> expectedDocIds) {
        if (sources == null || sources.isEmpty() || expectedDocIds.isEmpty()) {
            return false;
        }

        for (RagSourceVO source : sources) {
            if (source.getDocId() != null && expectedDocIds.contains(source.getDocId())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断关键词是否命中。
     *
     * 作用：
     * 当 expected_doc_ids 不好维护时，可以用 expected_keywords 做兜底。
     *
     * 判断范围：
     * 1. 来源标题 title
     * 2. 命中文本片段 contentPreview
     * 3. 场馆名 venueName
     * 4. 场地名 courtName
     */
    private boolean judgeKeywordHit(List<RagSourceVO> sources, Set<String> expectedKeywords) {
        if (sources == null || sources.isEmpty() || expectedKeywords.isEmpty()) {
            return false;
        }

        /*
         * 把所有命中来源中的可检索文本拼成一个大字符串，
         * 然后判断 expectedKeywords 中是否有关键词出现。
         */
        String allText = sources.stream()
                .map(source -> {
                    StringBuilder builder = new StringBuilder();

                    if (source.getTitle() != null) {
                        builder.append(source.getTitle()).append(" ");
                    }

                    if (source.getContentPreview() != null) {
                        builder.append(source.getContentPreview()).append(" ");
                    }

                    if (source.getVenueName() != null) {
                        builder.append(source.getVenueName()).append(" ");
                    }

                    if (source.getCourtName() != null) {
                        builder.append(source.getCourtName()).append(" ");
                    }

                    return builder.toString();
                })
                .collect(Collectors.joining(""));

        /*
         * 只要命中任意一个期望关键词，就认为 keywordHit = true。
         */
        for (String keyword : expectedKeywords) {
            if (StringUtils.hasText(keyword) && allText.contains(keyword)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 将英文逗号分隔的字符串解析成 Long 集合。
     *
     * 示例：
     * "1,2,3" -> Set<Long>{1,2,3}
     */
    private Set<Long> parseLongSet(String text) {
        Set<Long> result = new HashSet<>();

        if (!StringUtils.hasText(text)) {
            return result;
        }

        String[] arr = text.split(",");

        for (String item : arr) {
            try {
                result.add(Long.valueOf(item.trim()));
            } catch (Exception ignored) {
                /*
                 * 某个 ID 格式不合法时跳过。
                 * 例如 expectedDocIds = "1,abc,3"，
                 * abc 会被忽略，不影响整体评估。
                 */
            }
        }

        return result;
    }

    /**
     * 将英文逗号分隔的字符串解析成 String 集合。
     *
     * 示例：
     * "预约,退款,审核" -> Set<String>{"预约","退款","审核"}
     */
    private Set<String> parseStringSet(String text) {
        Set<String> result = new HashSet<>();

        if (!StringUtils.hasText(text)) {
            return result;
        }

        String[] arr = text.split(",");

        for (String item : arr) {
            if (StringUtils.hasText(item)) {
                result.add(item.trim());
            }
        }

        return result;
    }

    /**
     * 将 Long 列表拼接成英文逗号分隔的字符串。
     *
     * 示例：
     * [1,2,3] -> "1,2,3"
     *
     * 用于保存到 rag_eval_result.retrieved_doc_ids。
     */
    private String joinLongs(List<Long> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }

        return values.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }
}