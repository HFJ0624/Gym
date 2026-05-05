package com.sau.gym.admin.agent.store;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 作者:hfj
 * 功能:保存用户当前待确认的一次业务操作
 * 支持预约草稿、商品下单草稿
 * 增加 confirmToken，避免用户误触确认
 * 日期: 2026/4/23 14:43
 */
public record PendingDraft(
        PendingDraftType type,      // 草稿类型：预约 or 商城
        Map<String, Object> data,   // 草稿里的具体参数，比如 venueName/courtId/price 等
        LocalDateTime createdAt,     // 草稿创建时间
        String confirmToken //确认码
) implements Serializable {

}
