package com.sau.gym.admin.agent.model;

import lombok.Data;

/**
 * 作者:hfj
 * 功能:预约时间解析结果
 * 日期: 2026/5/6 8:47
 */
@Data
public class BookingTimeInfo {

    /**
     * 预约日期，格式 yyyy-MM-dd。
     */
    private String date;

    /**
     * 开始时间，格式 HH:mm:ss。
     */
    private String startTime;

    /**
     * 结束时间，格式 HH:mm:ss。
     */
    private String endTime;
}
