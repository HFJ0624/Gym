package com.sau.gym.model.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:登录成功响应结果实体类
 * 日期: 2026/3/4 15:24
 */
@Data
@Schema(description = "登录成功响应结果实体类")
public class LoginVo {

    @Schema(description = "令牌")
    private String token;

    @Schema(description = "刷新令牌,可以为空")
    private String refresh_token;

}
