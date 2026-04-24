package com.sau.gym.admin.agent.store;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/23 14:43
 */

/**
 * 待确认草稿对象
 * 作用：
 * 保存用户当前待确认的一次操作。
 */
public record PendingDraft(
        PendingDraftType type,      // 草稿类型：预约 or 商城
        Map<String, Object> data,   // 草稿里的具体参数，比如 venueName/courtId/price 等
        LocalDateTime createdAt     // 草稿创建时间
)  {

}
