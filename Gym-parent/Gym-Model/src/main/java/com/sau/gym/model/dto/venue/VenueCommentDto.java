package com.sau.gym.model.dto.venue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/30 10:18
 */
@Data
@Schema(description = "请求参数实体类")
public class VenueCommentDto {

    @Schema(description = "场馆名称")
    private String venueName;

    @Schema(description = "评论内容")
    private String content;
}
