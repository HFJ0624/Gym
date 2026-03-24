package com.sau.gym.model.dto.venue;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/24 10:44
 */
@Data
@Schema(description = "请求参数实体类")
public class BookingDto {

    private Long courtId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date bookingDate;

    private BigDecimal totalPrice;

    private String remark;

    private Long userId; // 当前登录用户ID
}
