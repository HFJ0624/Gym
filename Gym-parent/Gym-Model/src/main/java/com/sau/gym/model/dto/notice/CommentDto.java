package com.sau.gym.model.dto.notice;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/27 15:16
 */
@Data
@Schema(description = "请求参数实体类")
public class CommentDto {

    private Long userId;

    private Long noticeId;

    private String content;

    private Integer esteem;
}
