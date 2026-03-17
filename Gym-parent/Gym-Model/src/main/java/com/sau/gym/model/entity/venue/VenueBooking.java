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
 * 日期: 2026/3/17 9:17
 */
@Data
@Schema(description = "场馆预约实体类")
public class VenueBooking extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "场馆id")
    private Long venueId;

    @Schema(description = "预约日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date bookingDate;

    @Schema(description = "订单总金额")
    private Integer personNum;

    @Schema(description = "备注")
    private String remark;
}
