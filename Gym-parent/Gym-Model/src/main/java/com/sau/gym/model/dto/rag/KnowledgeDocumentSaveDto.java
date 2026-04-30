package com.sau.gym.model.dto.rag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/30 15:45
 */
@Data
@Schema(description = "保存知识文档请求参数")
public class KnowledgeDocumentSaveDto {

    @Schema(description = "知识标题")
    private String title;

    @Schema(description = "知识正文")
    private String content;

    @Schema(description = "知识范围：1平台级，2场馆级，3场地级，4公告级，5FAQ")
    private Integer knowledgeScope;

    /**
     * 来源类型：
     * 1平台规则，2预约规则，3退款规则，4场馆介绍，5场馆设施，
     * 6停车说明，7开放时间，8场地介绍，9场地设施，10场地价格，11公告，12FAQ
     */
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

    @Schema(description = "场地类型，例如：篮球场、足球场、羽毛球场")
    private String courtType;

    @Schema(description = "关联公告ID")
    private Long noticeId;

    @Schema(description = "主题")
    private String topic;

    @Schema(description = "标签，多个标签用逗号分隔")
    private String tags;

    @Schema(description = "优先级")
    private Integer priority;
}
