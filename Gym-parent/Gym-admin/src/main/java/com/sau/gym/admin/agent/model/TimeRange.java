package com.sau.gym.admin.agent.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:时间段解析结果
 * 日期: 2026/5/6 8:50
 */
@Data
@AllArgsConstructor
public class TimeRange {

    /**
     * 开始时间，格式 HH:mm:ss。
     */
    private String startTime;

    /**
     * 结束时间，格式 HH:mm:ss。
     */
    private String endTime;
}
