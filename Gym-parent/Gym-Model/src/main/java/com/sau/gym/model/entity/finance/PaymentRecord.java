package com.sau.gym.model.entity.finance;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/29 8:31
 */
@Data
@Schema(description = "支付流水表实体类")
public class PaymentRecord {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "支付流水号")
    private String payNo;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "订单类型：1预约订单，2商城订单")
    private Integer orderType;

    @Schema(description = "支付渠道：1余额，2支付宝，3微信")
    private Integer payChannel;

    @Schema(description = "支付金额")
    private BigDecimal amount;

    @Schema(description = "支付状态：0待支付，1支付成功，2支付失败")
    private Integer status;

    @Schema(description = "支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
