package com.sau.gym.admin.agent.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

/**
 * 作者:hfj
 * 功能:Agent可取消预约展示对象
 * 日期: 2026/5/9 10:37
 */
@Data
public class AgentCancelableBookingVO {

    /**
     * 预约订单ID。
     */
    private Long bookingId;

    /**
     * 订单编号。
     */
    private String orderNo;

    /**
     * 用户ID。
     */
    private Long userId;

    /**
     * 场馆名称。
     */
    private String venueName;

    /**
     * 场地ID。
     */
    private Long courtId;

    /**
     * 场地名称。
     */
    private String courtName;

    /**
     * 场地类型。
     */
    private String courtType;

    /**
     * 预约日期。
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate bookingDate;

    /**
     * 开始时间。
     */
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    /**
     * 结束时间。
     */
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    /**
     * 订单金额。
     */
    private BigDecimal totalPrice;

    /**
     * 订单状态：0待支付 1已支付 2已取消 3已完成。
     */
    private Integer status;

    /**
     * 备注。
     */
    private String remark;

    /**
     * 创建时间。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
