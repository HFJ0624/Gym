package com.sau.gym.model.vo.notice;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/24 17:11
 */
@Data
@Schema(description = "场馆公告返回类")
public class NoticeVO {

    @Schema(description = "公告id")
    private Long id;

    @Schema(description = "创建人名称")
    private String username;

    @Schema(description = "创建人头像")
    private String avatar;

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

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
