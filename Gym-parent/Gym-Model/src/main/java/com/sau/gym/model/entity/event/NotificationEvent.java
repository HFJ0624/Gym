package com.sau.gym.model.entity.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 作者:hfj
 * 功能:系统通知事件基类
 * 日期: 2026/4/29 16:39
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEvent {

    private Long userId;

    private String title;

    private String content;

    private Integer type;

    private Long businessId;

    private String businessNo;

    private Integer businessType;
}
