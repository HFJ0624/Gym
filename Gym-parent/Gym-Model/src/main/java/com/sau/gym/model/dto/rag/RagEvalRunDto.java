package com.sau.gym.model.dto.rag;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 作者:hfj
 * 功能:RAG 评估运行 DTO
 * 日期: 2026/5/9 19:28
 */
@Data
public class RagEvalRunDto {

    /**
     * TopK。
     */
    private Integer topK = 3;

    /**
     * 最低相似度。
     *
     * 如果返回来源为空，或者 maxScore < minScore，则认为 actualNoAnswer = true。
     */
    private BigDecimal minScore = BigDecimal.valueOf(0.5);

    /**
     * 分类过滤。
     *
     * 为空表示评估全部启用用例。
     */
    private String category;
}