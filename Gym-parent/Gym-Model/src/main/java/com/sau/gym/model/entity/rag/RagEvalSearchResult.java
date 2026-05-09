package com.sau.gym.model.entity.rag;

import com.sau.gym.model.vo.rag.RagSourceVO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:hfj
 * 功能:RAG评估用搜索结果
 * 日期: 2026/5/9 19:31
 */
@Data
public class RagEvalSearchResult {

    /**
     * 模型回答。
     */
    private String answer;

    /**
     * 命中来源。
     */
    private List<RagSourceVO> sources = new ArrayList<>();

    /**
     * 最高相似度。
     */
    private Double maxScore;

    /**
     * 最低相似度。
     */
    private Double minScore;
}
