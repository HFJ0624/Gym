package com.sau.gym.admin.agent.store;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/23 14:44
 */
@Component
public class AgentDraftStore {

    private final Map<Long, PendingDraft> cache = new ConcurrentHashMap<>();

    public void save(Long userId, PendingDraft draft) {
        cache.put(userId, draft);
    }

    public PendingDraft get(Long userId) {
        return cache.get(userId);
    }

    public void clear(Long userId) {
        cache.remove(userId);
    }

    public boolean hasDraft(Long userId) {
        return cache.containsKey(userId);
    }
}
