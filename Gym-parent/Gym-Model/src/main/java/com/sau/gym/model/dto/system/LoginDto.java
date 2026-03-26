package com.sau.gym.model.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:用户登录请求参数
 * 日期: 2026/3/4 15:23
 */
@Data
@Schema(description = "用户登录请求参数")
public class LoginDto {

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "提交验证码")
    private String captcha;

    @Schema(description = "验证码key")
    private String codeKey;

    @Schema(description = "用户手机号码")
    private String phone;

    @Schema(description = "手机验证码")
    private String phoneCode;

}
