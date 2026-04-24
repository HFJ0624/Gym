package com.sau.gym.admin.agent.store;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 作者:hfj
 * 功能:草稿缓存器。
 * 保存每个用户当前待确认的草稿。当前版本使用内存缓存，适合本地开发和单机运行。
 * 如果后面要做分布式部署，建议改成 Redis。
 * 日期: 2026/4/23 14:44
 */
@Component
public class AgentDraftStore {

    private final Map<Long, PendingDraft> cache = new ConcurrentHashMap<>();

    /**
     * 保存草稿
     */
    public void save(Long userId, PendingDraft draft) {
        cache.put(userId, draft);
    }

    /**
     * 获取草稿
     */
    public PendingDraft get(Long userId) {
        return cache.get(userId);
    }

    /**
     * 删除草稿
     */
    public void clear(Long userId) {
        cache.remove(userId);
    }

    /**
     * 判断用户当前是否有待确认草稿
     */
    public boolean hasDraft(Long userId) {
        return cache.containsKey(userId);
    }
}
