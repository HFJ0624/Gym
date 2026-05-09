package com.sau.gym.admin.rag.service.impl;

import com.sau.gym.admin.rag.service.RagEvalSearchService;
import com.sau.gym.admin.rag.service.RagQaService;
import com.sau.gym.model.dto.rag.RagAskDto;
import com.sau.gym.model.entity.rag.RagEvalSearchResult;
import com.sau.gym.model.vo.rag.RagAnswerVO;
import com.sau.gym.model.vo.rag.RagSourceVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 作者:hfj
 * 功能:RAG 评估搜索服务实现
 * 作用：
 * 专门给 RAG 评估调用你现有的 RAG 问答能力。
 * 日期: 2026/5/9 19:32
 */
@Service
public class RagEvalSearchServiceImpl implements RagEvalSearchService {

    private final RagQaService ragQaService;

    public RagEvalSearchServiceImpl(RagQaService ragQaService) {
        this.ragQaService = ragQaService;
    }

    @Override
    public RagEvalSearchResult search(String question, Integer topK, Double minScore) {

        //1.构造RAG问答请求DTO。
        //评估模块只负责：1.批量传入标准问题  2.接收RAG返回结果  3.根据返回来源计算Top1/TopK/来源正确率/无答案率
        RagAskDto dto = new RagAskDto();
        dto.setQuestion(question);

        //2.调用现有RAG问答服务。
        RagAnswerVO qaVO = ragQaService.ask(dto);

        //3.构造评估专用返回对象。
        RagEvalSearchResult result = new RagEvalSearchResult();

        //4.如果RAG服务没有返回结果，则认为：1.回答为空  2.命中来源为空
        if (qaVO == null) {
            result.setAnswer("");
            result.setSources(new ArrayList<>());
            return result;
        }

        //5.保存模型最终回答。
        result.setAnswer(qaVO.getAnswer());

        //6.获取 RAG 命中来源。
        List<RagSourceVO> sources = qaVO.getSources() == null
                ? new ArrayList<>()
                : qaVO.getSources();

        //7.只保留前 TopK 个命中来源。评估 TopK 命中率时，只应该看前 K 个结果。
        if (topK != null && topK > 0 && sources.size() > topK) {
            sources = sources.subList(0, topK);
        }

        //8.保存截断后的命中来源。
        result.setSources(sources);

        //9.计算最高相似度。
        Double maxScore = sources.stream()
                .filter(item -> item.getScore() != null)
                .map(RagSourceVO::getScore)
                .max(Comparator.naturalOrder())
                .orElse(null);

        //10.计算最低命中相似度。
        Double minHitScore = sources.stream()
                .filter(item -> item.getScore() != null)
                .map(RagSourceVO::getScore)
                .min(Comparator.naturalOrder())
                .orElse(null);

        //11.写入评估搜索结果。
        result.setMaxScore(maxScore);
        result.setMinScore(minHitScore);

        return result;
    }
}
