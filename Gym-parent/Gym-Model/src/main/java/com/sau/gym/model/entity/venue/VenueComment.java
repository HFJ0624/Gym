package com.sau.gym.model.entity.venue;

import com.sau.gym.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/30 10:17
 */
@Data
@Schema(description = "场馆评论实体类")
public class VenueComment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "场馆Id")
    private Long venueId;

    @Schema(description = "用户Id")
    private String userId;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "是否好评:5,好评;4:良好;3:中等;2:一般;1:差")
    private Integer esteem;

    @Schema(description = "1:通过,2:屏蔽")
    private Integer status;
}
