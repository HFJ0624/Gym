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

        //1. limitSeconds <= 0 表示关闭限流。
        //用途:开发测试阶段可以把 RAG 工具设置为 0，避免调试时频繁被拦。
        if (limitSeconds <= 0) {
            return true;
        }


        //2. 工具编码不能为空。
        //toolCode 是 Redis key 的一部分，如果为空就无法正确限流。
        if (toolCode == null || toolCode.trim().isEmpty()) {
            return true;
        }

        /*
         *3. 构造限流用户标识。
         *
         * 登录用户:
         * 使用 userId 限流。
         *
         * 未登录用户:
         * 这里先使用 guest 兜底，避免 userId == null 时直接误判为过于频繁。
         *
         * 注意:
         * 更严格的生产方案应该用 IP、设备指纹、匿名 visitorId。
         * 但是你当前项目里没有这些参数，所以先用 guest 兜底即可。
         */
        String userKey = userId == null ? "guest" : String.valueOf(userId);

        //4. Redis 限流 key。
        String key = "agent:tool:rate:" + userKey + ":" + toolCode;

        /*
         * 5. setIfAbsent 等价于 Redis SETNX。
         *
         * 如果 key 不存在:
         * 说明用户在限流窗口内没有调用过该工具，允许调用。
         *
         * 如果 key 已存在:
         * 说明用户在 limitSeconds 秒内重复调用了该工具，拒绝调用。
         */
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(key, "1", Duration.ofSeconds(limitSeconds));

        return Boolean.TRUE.equals(success);
    }
}
