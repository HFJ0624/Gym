package com.sau.gym.model.dto.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/24 17:09
 */
@Data
@Schema(description = "请求参数实体类")
public class NoticeDto {

    @Schema(description = "公告标题")
    private String title;

    @Schema(description = "公告内容")
    private String content;

    @Schema(description = "类型 1=系统公告 2=活动通知 3=新闻")
    private Integer noticeType;
}
