package com.sau.gym.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/9 21:00
 */
@Data
@Schema(description = "请求参数实体类")
public class UserBalanceDto {

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "用户充值金额")
    private BigDecimal amount;
}
