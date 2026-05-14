package com.sau.gym.admin.agent.service;

public interface AgentToolRateLimitService {

    /**
     * 尝试通过限流。
     *
     * @param userId 用户ID
     * @param toolCode 工具编码
     * @param limitSeconds 限流秒数
     * @return true 表示允许，false 表示被限流
     */
    boolean tryAcquire(Long userId, String toolCode, int limitSeconds);
}
