package com.sau.gym.admin.agent.store;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
/**
 * 作者:hfj
 * 功能:草稿缓存器。
 * 使用 Redis 保存草稿，并设置过期时间。
 * 日期: 2026/4/23 14:44
 */
@Component
public class AgentDraftStore {

    //Redis key的前缀
    private static final String KEY_PREFIX = "agent:draft:user:";

    private final RedisTemplate<String, PendingDraft> redisTemplate;

    /**
     * 草稿过期时间，单位：分钟。
     * 默认 15 分钟。
     */
    @Value("${gym.agent.draft.ttl-minutes:15}")
    private Long ttlMinutes;

    public AgentDraftStore(RedisTemplate<String, PendingDraft> agentDraftRedisTemplate) {
        this.redisTemplate = agentDraftRedisTemplate;
    }

    /**
     * 保存草稿。
     *
     * @param userId 用户ID
     * @param draft 草稿对象
     */
    public void save(Long userId, PendingDraft draft) {
        if (userId == null || draft == null) {
            return;
        }

        String key = buildKey(userId);

        //写入 Redis，并设置过期时间。
        redisTemplate.opsForValue().set(
                key,
                draft,
                Duration.ofMinutes(ttlMinutes)
        );

    }

    /**
     * 获取草稿。
     *
     * @param userId 用户ID
     * @return 草稿对象
     */
    public PendingDraft get(Long userId) {
        if (userId == null) {
            return null;
        }

        return redisTemplate.opsForValue().get(buildKey(userId));
    }

    /**
     * 删除草稿。
     * 用户确认成功或取消操作后调用。
     */
    public void clear(Long userId) {
        if (userId == null) {
            return;
        }

        redisTemplate.delete(buildKey(userId));
    }

    /**
     * 判断用户当前是否有待确认草稿。
     */
    public boolean hasDraft(Long userId) {
        if (userId == null) {
            return false;
        }

        Boolean hasKey = redisTemplate.hasKey(buildKey(userId));

        return Boolean.TRUE.equals(hasKey);
    }

    /**
     * 获取草稿剩余过期时间，单位：秒。
     * 可以用于后续前端提示：
     * “请在 15 分钟内确认。”
     */
    public Long getExpireSeconds(Long userId) {
        if (userId == null) {
            return -1L;
        }

        return redisTemplate.getExpire(buildKey(userId));
    }

    /**
     * 构造 Redis key。
     */
    private String buildKey(Long userId) {
        return KEY_PREFIX + userId;
    }
}
