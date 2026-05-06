package com.sau.gym.model.vo.court;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/6 9:39
 */
@Data
@Schema(description = "可预约时段VO")
public class AvailableTimeSlotVO {

    @Schema(description = "预约日期，格式 yyyy-MM-dd")
    private String date;

    @Schema(description = "开始时间，格式 HH:mm:ss")
    private String startTime;

    @Schema(description = "结束时间，格式 HH:mm:ss")
    private String endTime;

    @Schema(description = "前端展示文本")
    private String label;

    @Schema(description = "是否可预约")
    private Boolean available;

    @Schema(description = "不可预约原因")
    private String reason;
}
