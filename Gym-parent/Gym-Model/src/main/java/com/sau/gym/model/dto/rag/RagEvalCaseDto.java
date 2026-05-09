package com.sau.gym.model.dto.rag;

import lombok.Data;

/**
 * 作者:hfj
 * 功能:RAG 评估用例 DTO
 * 日期: 2026/5/9 19:28
 */
@Data
public class RagEvalCaseDto {

    private Long id;

    private String question;

    private String expectedDocIds;

    private String expectedKeywords;

    private Integer expectedNoAnswer;

    private String category;

    private Integer enabled;

    private String remark;
}
