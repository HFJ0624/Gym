package com.sau.gym.admin.agent.store;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/23 14:43
 */
public record PendingDraft(
        PendingDraftType type,
        Map<String, Object> data,
        LocalDateTime createdAt
)  {

}
