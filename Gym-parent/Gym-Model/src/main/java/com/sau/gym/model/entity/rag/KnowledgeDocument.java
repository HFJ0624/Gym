package com.sau.gym.model.entity.rag;

import com.sau.gym.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/30 15:38
 */
@Data
@Schema(description = "RAG知识文档实体类")
public class KnowledgeDocument extends BaseEntity {

    private static final long serialVersionUID = 1L;

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

    @Schema(description = "主题，例如：停车、开放时间、退款、篮球、羽毛球")
    private String topic;

    @Schema(description = "标签，多个标签用逗号分隔")
    private String tags;

    @Schema(description = "优先级，数值越大越优先")
    private Integer priority;

    @Schema(description = "是否启用：0禁用，1启用")
    private Integer enabled;

    @Schema(description = "索引状态：0未入库，1已入库")
    private Integer indexedStatus;

}
