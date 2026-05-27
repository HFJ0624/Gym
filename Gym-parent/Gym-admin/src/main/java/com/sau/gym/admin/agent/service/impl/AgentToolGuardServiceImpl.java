package com.sau.gym.admin.agent.service.impl;

import com.sau.gym.admin.agent.service.AgentToolGuardService;
import com.sau.gym.admin.agent.service.AgentToolRateLimitService;
import com.sau.gym.admin.enums.AgentRiskLevel;
import org.springframework.stereotype.Service;

/**
 * 作者:hfj
 * 功能: Agent 工具风控服务实现
 * 第一版做三件事：
 * 1. 登录校验
 * 2. 高风险工具拦截
 * 3. Redis 限流
 * 日期: 2026/5/10 18:39
 */
@Service
public class AgentToolGuardServiceImpl implements AgentToolGuardService {

    private final AgentToolRateLimitService rateLimitService;

    public AgentToolGuardServiceImpl(AgentToolRateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @Override
    public String checkBeforeToolCall(Long userId,
                                      String toolCode,
                                      String toolName,
                                      AgentRiskLevel riskLevel,
                                      boolean requireLogin,
                                      boolean requireConfirm,
                                      int limitSeconds) {

        //1. 登录校验。
        if (requireLogin && userId == null) {
            return "请先登录后再使用【" + toolName + "】功能。";
        }

        //2. 高风险工具拦截。
        if (requireConfirm || AgentRiskLevel.HIGH.equals(riskLevel)) {
            return "【" + toolName + "】属于高风险操作，不能由 Agent 直接执行，请先生成草稿并由用户确认。";
        }

        //3. 工具限流。防止同一个用户短时间内重复调用同一个工具。
        boolean allowed = rateLimitService.tryAcquire(
                userId,
                toolCode,
                limitSeconds
        );

        if (!allowed) {
            return "你调用【" + toolName + "】过于频繁，请稍后再试。";
        }

        //4.返回null表示允许继续执行真实工具逻辑。
        return null;
    }
}
