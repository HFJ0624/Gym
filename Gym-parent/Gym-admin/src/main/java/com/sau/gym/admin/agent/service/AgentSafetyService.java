package com.sau.gym.admin.agent.service;

import com.sau.gym.admin.agent.security.AgentSafetyCheckResult;

public interface AgentSafetyService {

    /**
     * 检查用户输入是否安全。
     *
     * @param userId 用户ID
     * @param message 用户输入
     * @return 检查结果
     */
    AgentSafetyCheckResult checkUserMessage(Long userId, String message);
}
