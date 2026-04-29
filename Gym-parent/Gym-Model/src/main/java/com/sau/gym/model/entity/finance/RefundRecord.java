package com.sau.gym.model.entity.finance;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/29 9:04
 */
@Data
@Schema(description = "退款流水表实体类")
public class RefundRecord {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "退款流水号")
    private String refundNo;

    @Schema(description = "原支付流水号")
    private String payNo;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "订单类型：1预约订单，2商城订单")
    private Integer orderType;

    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    @Schema(description = "退款状态：0退款中，1退款成功，2退款失败")
    private Integer status;

    @Schema(description = "退款时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date refundTime;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
