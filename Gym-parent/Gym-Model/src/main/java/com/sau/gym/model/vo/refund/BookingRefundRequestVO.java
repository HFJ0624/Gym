package com.sau.gym.model.vo.refund;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 作者:hfj
 * 功能:后台退款申请展示
 * 日期: 2026/5/9 11:17
 */
@Data
public class BookingRefundRequestVO {

    private Long id;

    private Long bookingId;

    private String orderNo;

    private Long userId;

    private String username;

    private String phone;

    private String venueName;

    private String courtName;

    private String courtType;

    private BigDecimal refundAmount;

    private String reason;

    /**
     * 退款状态：0待审核 1已通过 2已拒绝
     */
    private Integer status;

    private Long auditUserId;

    private String auditRemark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
