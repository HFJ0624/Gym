package com.sau.gym.model.dto.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/26 16:38
 */
@Data
@Schema(description = "请求参数实体类")
public class NoticeCommentDto {

    @Schema(description = "评论人名称")
    private String username;

    @Schema(description = "评论内容")
    private String content;
}
