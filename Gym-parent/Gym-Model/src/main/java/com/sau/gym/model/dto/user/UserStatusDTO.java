package com.sau.gym.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/5 19:56
 */
@Data
@Schema(description = "请求参数实体类")
public class UserStatusDTO {

    @Schema(description = "用户id")
    private Long id;

    @Schema(description = "用户状态")
    private String status;
}
