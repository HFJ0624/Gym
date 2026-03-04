package com.sau.gym.model.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/4 16:42
 */
@Data
@Schema(description = "验证码响应结果实体类")
public class ValidateCodeVo {

    //验证码的key
    @Schema(description = "验证码key")
    private String codeKey;

    //图片验证码对应的字符串数据
    @Schema(description = "验证码value")
    private String codeValue;
}
