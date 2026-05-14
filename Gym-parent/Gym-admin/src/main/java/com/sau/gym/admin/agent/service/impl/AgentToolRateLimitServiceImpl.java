package com.sau.gym.admin.agent.service.impl;

import com.sau.gym.admin.agent.service.AgentToolRateLimitService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 作者:hfj
 * 功能:Agent 工具 Redis 限流实现
 * 第一版限流策略：
 * 同一个用户 + 同一个工具，在 limitSeconds 秒内只能调用一次。
 * 日期: 2026/5/10 18:07
 */
@Service
public class AgentToolRateLimitServiceImpl implements AgentToolRateLimitService {

    private final StringRedisTemplate stringRedisTemplate;

    public AgentToolRateLimitServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean tryAcquire(Long userId, String toolCode, int limitSeconds) {
        //limitSeconds <= 0 表示不启用限流。
        if (limitSeconds <= 0) {
            return true;
        }

        if (userId == null || toolCode == null || toolCode.trim().isEmpty()) {
            return false;
        }

        String key = "agent:tool:rate:" + userId + ":" + toolCode;

        /*
         * setIfAbsent 等价于 Redis SETNX。
         *
         * 如果 key 不存在：
         * 1. 写入 key
         * 2. 设置过期时间
         * 3. 返回 true
         *
         * 如果 key 已存在：
         * 说明用户在限流窗口内重复调用
         * 返回 false
         */
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(key, "1", Duration.ofSeconds(limitSeconds));

        return Boolean.TRUE.equals(success);
    }
}
