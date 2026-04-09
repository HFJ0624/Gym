package com.sau.gym.model.entity.user;

import com.sau.gym.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/9 20:07
 */
@Data
@Schema(description = "用户账户余额实体类")
public class UserBalance extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户Id")
    private Long userId;

    @Schema(description = "用户余额")
    private BigDecimal balance;

}
