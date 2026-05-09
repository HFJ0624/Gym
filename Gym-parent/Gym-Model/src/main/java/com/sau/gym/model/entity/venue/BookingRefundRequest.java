package com.sau.gym.model.entity.venue;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sau.gym.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/9 10:36
 */
@Data
@Schema(description = "预约退款申请实体")
public class BookingRefundRequest extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "预约订单ID")
    private Long bookingId;

    @Schema(description = "预约订单编号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    @Schema(description = "取消或退款原因")
    private String reason;

    @Schema(description = "退款状态：0待审核 1已通过 2已拒绝")
    private Integer status;

    @Schema(description = "审核管理员ID")
    private Long auditUserId;

    @Schema(description = "审核备注")
    private String auditRemark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "审核时间")
    private Date auditTime;
}
