package com.sau.gym.model.entity.rag;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/30 15:43
 */
@Data
@Schema(description = "RAG检索日志实体")
public class RagSearchLog {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "提问用户ID，未登录时可以为空")
    private Long userId;

    @Schema(description = "用户原始问题")
    private String question;

    @Schema(description = "模型最终回答")
    private String answer;

    @Schema(description = "命中的知识来源，JSON字符串")
    private String matchedSources;

    @Schema(description = "最高相似度")
    private BigDecimal maxScore;

    @Schema(description = "最低相似度")
    private BigDecimal minScore;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Schema(description = "Agent调用链ID")
    private String traceId;
}
