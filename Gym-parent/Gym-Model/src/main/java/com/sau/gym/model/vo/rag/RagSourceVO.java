package com.sau.gym.model.vo.rag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/30 15:49
 */
@Data
@Schema(description = "RAG问答返回结果")
public class RagSourceVO {

    @Schema(description = "知识文档ID")
    private Long docId;

    @Schema(description = "知识标题")
    private String title;

    @Schema(description = "知识范围")
    private Integer knowledgeScope;

    @Schema(description = "知识范围名称")
    private String knowledgeScopeName;

    @Schema(description = "来源类型")
    private Integer sourceType;

    @Schema(description = "关联场馆ID")
    private Long venueId;

    @Schema(description = "关联场馆名称")
    private String venueName;

    @Schema(description = "关联场地ID")
    private Long courtId;

    @Schema(description = "关联场地名称")
    private String courtName;

    @Schema(description = "场地类型")
    private String courtType;

    @Schema(description = "主题")
    private String topic;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "相似度分数")
    private Double score;

    @Schema(description = "命中的文本片段预览")
    private String contentPreview;
}
