package com.sau.gym.admin.rag.service.impl;

import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import com.alibaba.fastjson.JSON;
import com.sau.gym.admin.mapper.RagSearchLogMapper;
import com.sau.gym.admin.rag.service.RagQaService;
import com.sau.gym.model.dto.rag.RagAskDto;
import com.sau.gym.model.entity.rag.RagSearchLog;
import com.sau.gym.model.entity.user.User;
import com.sau.gym.model.vo.rag.RagAnswerVO;
import com.sau.gym.model.vo.rag.RagSourceVO;
import com.sau.gym.utils.AuthContextUtil;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 作者:hfj
 * 功能:RAG问答服务实现。
 * 日期: 2026/4/30 16:34
 */
@Service
public class RagQaServiceImpl implements RagQaService {

    /**
     * RAG 专用 embedding 模型。
     */
    @Resource(name = "ragEmbeddingModel")
    private EmbeddingModel ragEmbeddingModel;

    @Autowired
    private EmbeddingStore<TextSegment> ragEmbeddingStore;

    @Autowired
    private ChatModel ragChatModel;

    @Autowired
    private RagSearchLogMapper ragSearchLogMapper;

    @Value("${gym.rag.retrieval.max-results}")
    private Integer maxResults;

    @Value("${gym.rag.retrieval.min-score}")
    private Double minScore;

    @Override
    public RagAnswerVO ask(RagAskDto dto) {
        if (dto == null || dto.getQuestion() == null || dto.getQuestion().trim().isEmpty()) {
            throw new RuntimeException("问题不能为空");
        }

        String question = dto.getQuestion().trim();

        // 1. 将用户问题转成 embedding 向量
        Embedding questionEmbedding = ragEmbeddingModel.embed(question).content();

        // 2. 构造向量检索请求
        // 这里先多召回一些，然后在 Java 层根据 venueId/courtId 做简单过滤。
        // 第一版这样最稳，不依赖 pgvector metadata filter 的具体 API。
        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(questionEmbedding)
                .maxResults(maxResults * 5)
                .minScore(minScore)
                .build();

        // 3. 从 pgvector 中检索最相关的知识片段
        EmbeddingSearchResult<TextSegment> searchResult = ragEmbeddingStore.search(searchRequest);

        // 4. 取出匹配结果
        List<EmbeddingMatch<TextSegment>> matches = searchResult.matches();

        // 5. 根据当前场馆/场地上下文做过滤
        List<EmbeddingMatch<TextSegment>> scopedMatches = filterByScope(matches, dto);

        // 6. 如果过滤后没有结果，就退回使用全局检索结果
        // 这样可以避免用户在某个场馆页提问平台规则时查不到答案。
        List<EmbeddingMatch<TextSegment>> finalMatches;
        if (scopedMatches == null || scopedMatches.isEmpty()) {
            finalMatches = matches;
        } else {
            finalMatches = scopedMatches;
        }

        // 7. 限制最终进入 prompt 的数量
        if (finalMatches != null && finalMatches.size() > maxResults) {
            finalMatches = finalMatches.subList(0, maxResults);
        }

        // 8. 没有检索到有效知识时，直接拒答，避免模型胡编
        if (finalMatches == null || finalMatches.isEmpty()) {
            RagAnswerVO vo = new RagAnswerVO();
            vo.setAnswer("知识库中暂无与该问题相关的信息，请换一种问法，或联系管理员补充场馆或场地知识。");
            vo.setSources(new ArrayList<>());

            saveSearchLog(question, vo.getAnswer(), vo.getSources(), null, null);
            return vo;
        }

        // 9. 构造知识库上下文
        String context = buildContext(finalMatches);

        // 10. 构造 Prompt
        String prompt = buildPrompt(context, question);

        // 11. 调用大模型生成回答
        String answer = ragChatModel.chat(prompt);

        // 12. 构造引用来源
        List<RagSourceVO> sources = buildSources(finalMatches);

        // 13. 统计最高分和最低分，用于日志记录
        Double maxScoreValue = finalMatches.stream()
                .map(EmbeddingMatch::score)
                .max(Double::compareTo)
                .orElse(null);

        Double minScoreValue = finalMatches.stream()
                .map(EmbeddingMatch::score)
                .min(Double::compareTo)
                .orElse(null);

        // 14. 保存 RAG 检索日志
        saveSearchLog(question, answer, sources, maxScoreValue, minScoreValue);

        // 15. 返回结果
        RagAnswerVO vo = new RagAnswerVO();
        vo.setAnswer(answer);
        vo.setSources(sources);
        return vo;
    }

    /**
     * 构造上下文。
     * 把检索到的多个知识片段拼接成模型可以阅读的格式。
     */
    private String buildContext(List<EmbeddingMatch<TextSegment>> matches) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < matches.size(); i++) {
            TextSegment segment = matches.get(i).embedded();

            String title = segment.metadata().getString("title");
            String venueName = segment.metadata().getString("venueName");
            String courtName = segment.metadata().getString("courtName");
            String courtType = segment.metadata().getString("courtType");
            String topic = segment.metadata().getString("topic");

            builder.append("【资料").append(i + 1).append("】\n");

            builder.append("标题：").append(title).append("\n");

            if (venueName != null && !venueName.isEmpty()) {
                builder.append("关联场馆：").append(venueName).append("\n");
            }

            if (courtName != null && !courtName.isEmpty()) {
                builder.append("关联场地：").append(courtName).append("\n");
            }

            if (courtType != null && !courtType.isEmpty()) {
                builder.append("场地类型：").append(courtType).append("\n");
            }

            if (topic != null && !topic.isEmpty()) {
                builder.append("主题：").append(topic).append("\n");
            }

            builder.append("内容：").append(segment.text()).append("\n\n");
        }

        return builder.toString();
    }

    /**
     * 构造 Prompt。
     *
     * 这里最关键的是约束模型：
     * 1. 只能根据上下文回答
     * 2. 上下文没有答案就说不知道
     * 3. 不允许编造场馆信息
     */
    private String buildPrompt(String context, String question) {
        return """
                你是体育场馆预约平台的知识库问答助手。

                你必须严格根据【知识库资料】回答用户问题。
                如果知识库资料中没有相关信息，请回答：“知识库中暂无相关信息。”
                不允许编造场馆名称、开放时间、价格、设施、停车信息。
                回答要简洁、准确，适合普通用户阅读。

                【知识库资料】
                %s

                【用户问题】
                %s

                【回答要求】
                1. 先直接回答问题。
                2. 如果涉及规则、开放时间、停车、设施，请说明依据。
                3. 不要输出无关内容。
                """
                .formatted(context, question);
    }

    /**
     * 构造引用来源列表。
     */
    private List<RagSourceVO> buildSources(List<EmbeddingMatch<TextSegment>> matches) {
        List<RagSourceVO> sources = new ArrayList<>();

        for (EmbeddingMatch<TextSegment> match : matches) {
            TextSegment segment = match.embedded();

            RagSourceVO source = new RagSourceVO();

            source.setDocId(parseLong(segment.metadata().getString("docId")));
            source.setTitle(segment.metadata().getString("title"));

            source.setKnowledgeScope(parseInteger(segment.metadata().getString("knowledgeScope")));
            source.setKnowledgeScopeName(getKnowledgeScopeName(source.getKnowledgeScope()));

            source.setSourceType(parseInteger(segment.metadata().getString("sourceType")));

            source.setVenueId(parseLong(segment.metadata().getString("venueId")));
            source.setVenueName(segment.metadata().getString("venueName"));

            source.setCourtId(parseLong(segment.metadata().getString("courtId")));
            source.setCourtName(segment.metadata().getString("courtName"));
            source.setCourtType(segment.metadata().getString("courtType"));

            source.setTopic(segment.metadata().getString("topic"));
            source.setTags(segment.metadata().getString("tags"));

            source.setScore(match.score());
            source.setContentPreview(preview(segment.text(), 120));

            sources.add(source);
        }

        return sources;
    }

    /**
     * 保存 RAG 检索日志。
     */
    private void saveSearchLog(String question,
                               String answer,
                               List<RagSourceVO> sources,
                               Double maxScore,
                               Double minScore) {
        try {
            User user = AuthContextUtil.get();

            RagSearchLog log = new RagSearchLog();
            log.setUserId(user == null ? null : user.getId());
            log.setQuestion(question);
            log.setAnswer(answer);
            log.setMatchedSources(JSON.toJSONString(sources));
            log.setMaxScore(maxScore == null ? null : BigDecimal.valueOf(maxScore));
            log.setMinScore(minScore == null ? null : BigDecimal.valueOf(minScore));

            ragSearchLogMapper.insert(log);
        } catch (Exception e) {
            // 日志失败不能影响用户问答，所以这里只打印，不抛出
            System.out.println("[RAG] 保存检索日志失败：" + e.getMessage());
        }
    }

    /**
     * 字符串转 Long。
     */
    private Long parseLong(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            return Long.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 字符串转 Integer。
     */
    private Integer parseInteger(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 文本预览。
     */
    private String preview(String text, int maxLength) {
        if (text == null) {
            return "";
        }

        if (text.length() <= maxLength) {
            return text;
        }

        return text.substring(0, maxLength) + "...";
    }

    /**
     * 根据场馆ID、场地ID过滤检索结果。
     *
     * 过滤策略：
     * 1. 如果传了 courtId，优先保留：
     *    - 当前 courtId 的场地级知识
     *    - 当前 venueId 的场馆级知识
     *    - 平台级知识/FAQ
     *
     * 2. 如果只传了 venueId，优先保留：
     *    - 当前 venueId 的场馆级知识
     *    - 该 venueId 下的场地知识
     *    - 平台级知识/FAQ
     *
     * 3. 如果没有传 venueId/courtId，不过滤。
     */
    private List<EmbeddingMatch<TextSegment>> filterByScope(List<EmbeddingMatch<TextSegment>> matches, RagAskDto dto) {
        if (matches == null || matches.isEmpty()) {
            return matches;
        }

        // 没有上下文，不过滤
        if (dto.getVenueId() == null && dto.getCourtId() == null) {
            return matches;
        }

        List<EmbeddingMatch<TextSegment>> result = new ArrayList<>();

        for (EmbeddingMatch<TextSegment> match : matches) {
            TextSegment segment = match.embedded();

            Long metadataVenueId = parseLong(segment.metadata().getString("venueId"));
            Long metadataCourtId = parseLong(segment.metadata().getString("courtId"));
            Integer knowledgeScope = parseInteger(segment.metadata().getString("knowledgeScope"));

            boolean keep = false;

            // 平台级和 FAQ 一律保留，因为它们适用于所有场馆/场地
            if (knowledgeScope != null && (knowledgeScope == 1 || knowledgeScope == 5)) {
                keep = true;
            }

            // 如果当前问题带了 courtId，优先匹配具体场地
            if (dto.getCourtId() != null && dto.getCourtId().equals(metadataCourtId)) {
                keep = true;
            }

            // 如果当前问题带了 venueId，匹配当前场馆
            if (dto.getVenueId() != null && dto.getVenueId().equals(metadataVenueId)) {
                keep = true;
            }

            if (keep) {
                result.add(match);
            }
        }

        return result;
    }

    /**
     * 获取知识范围名称。
     */
    private String getKnowledgeScopeName(Integer scope) {
        if (scope == null) {
            return "未知";
        }

        switch (scope) {
            case 1:
                return "平台级知识";
            case 2:
                return "场馆级知识";
            case 3:
                return "场地级知识";
            case 4:
                return "公告级知识";
            case 5:
                return "常见问题";
            default:
                return "未知";
        }
    }
}
