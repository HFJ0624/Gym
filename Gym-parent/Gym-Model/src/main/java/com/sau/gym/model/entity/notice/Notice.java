package com.sau.gym.model.entity.notice;

import com.sau.gym.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/24 17:03
 */
@Data
@Schema(description = "公告实体类")
public class Notice extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "创建人id")
    private Long createId;

    @Schema(description = "公告标题")
    private String title;

    @Schema(description = "公告内容")
    private String content;

    @Schema(description = "评论数量")
    private Integer nums;

    @Schema(description = "类型 1=系统公告 2=活动通知 3=新闻")
    private Integer noticeType;

    @Schema(description = "状态 0=草稿 1=已发布 2=已下架")
    private Integer status;

}
