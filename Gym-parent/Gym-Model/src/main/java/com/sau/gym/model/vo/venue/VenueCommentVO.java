package com.sau.gym.model.vo.venue;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/30 10:19
 */
@Data
@Schema(description = "场馆评论返回类")
public class VenueCommentVO {

    @Schema(description = "评论id")
    private Long id;

    @Schema(description = "评论人名称")
    private String username;

    @Schema(description = "评论人头像")
    private String avatar;

    @Schema(description = "对应场馆名称")
    private String venueName;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "是否好评")
    private Integer esteem;

    @Schema(description = "状态 1=通过 2=屏蔽")
    private Integer status;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
