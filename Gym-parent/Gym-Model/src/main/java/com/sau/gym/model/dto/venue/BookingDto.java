package com.sau.gym.model.dto.venue;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

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
    private LocalDate bookingDate;

    @Schema(description = "开始时间，例如 19:00:00")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @Schema(description = "结束时间，例如 20:00:00")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    private BigDecimal hoursPrice;

    private String remark;

    private Long userId; // 当前登录用户ID
}
