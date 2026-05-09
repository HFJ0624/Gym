package com.sau.gym.model.entity.rag;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 作者:hfj
 * 功能:RAG 评估运行批次实体
 * 日期: 2026/5/9 19:26
 */
@Data
public class RagEvalRun {

    private Long id;

    private String runNo;

    private Integer topK;

    private BigDecimal minScore;

    private Integer totalCount;

    private Integer answerableCount;

    private Integer noAnswerExpectedCount;

    private Integer top1HitCount;

    private Integer topkHitCount;

    private Integer sourceCorrectCount;

    private Integer noAnswerCount;

    private Integer noAnswerCorrectCount;

    private BigDecimal top1HitRate;

    private BigDecimal topkHitRate;

    private BigDecimal sourceCorrectRate;

    private BigDecimal noAnswerRate;

    private BigDecimal noAnswerAccuracy;

    private BigDecimal avgMaxScore;

    private BigDecimal avgCostMs;

    private String status;

    private String errorMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;
}
