package com.sau.gym.model.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/28 8:46
 */
@Data
@Schema(description = "签到请求参数")
public class SignInDto {

    private String name;

    private String phone;
}
