package com.sau.gym.model.entity.finance;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/28 17:14
 */
@Data
@Schema(description = "余额流水表实体类")
public class BalanceRecord {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "变动余额")
    private String orderNo;

    @Schema(description = "变动余额")
    private BigDecimal amount;

    @Schema(description = "变动前余额")
    private BigDecimal beforeBalance;

    @Schema(description = "变动后余额")
    private BigDecimal afterBalance;

    @Schema(description = "流水类型：1充值，2预约支付，3预约退款")
    private Integer type;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
