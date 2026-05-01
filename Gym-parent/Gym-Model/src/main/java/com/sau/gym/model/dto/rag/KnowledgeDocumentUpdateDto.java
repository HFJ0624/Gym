package com.sau.gym.model.dto.rag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/30 22:11
 */
@Data
@Schema(description = "RAG知识文档查询参数")
public class KnowledgeDocumentUpdateDto {

    @Schema(description = "知识ID")
    private Long id;

    @Schema(description = "知识标题")
    private String title;

    @Schema(description = "知识正文")
    private String content;

    @Schema(description = "知识范围")
    private Integer knowledgeScope;

    @Schema(description = "来源类型")
    private Integer sourceType;

    @Schema(description = "场馆ID")
    private Long venueId;

    @Schema(description = "场馆名称")
    private String venueName;

    @Schema(description = "场地ID")
    private Long courtId;

    @Schema(description = "场地名称")
    private String courtName;

    @Schema(description = "场地类型")
    private String courtType;

    @Schema(description = "公告ID")
    private Long noticeId;

    @Schema(description = "主题")
    private String topic;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "优先级")
    private Integer priority;

    @Schema(description = "是否启用")
    private Integer enabled;

}
