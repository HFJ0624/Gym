package com.sau.gym.model.entity.rag;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 作者:hfj
 * 功能:RAG 评估明细结果实体
 * 日期: 2026/5/9 19:27
 */
@Data
public class RagEvalResult {

    private Long id;

    private Long runId;

    private Long caseId;

    private String question;

    private String expectedDocIds;

    private String expectedKeywords;

    private Integer expectedNoAnswer;

    private String retrievedDocIds;

    private String matchedSources;

    private BigDecimal maxScore;

    private Integer top1Hit;

    private Integer topkHit;

    private Integer sourceCorrect;

    private Integer actualNoAnswer;

    private Integer noAnswerCorrect;

    private String answerText;

    private Long costMs;

    private String errorMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
