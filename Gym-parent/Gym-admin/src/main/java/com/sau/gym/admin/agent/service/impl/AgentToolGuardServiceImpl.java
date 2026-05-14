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

        /*
         * 1. 登录校验。
         *
         * 查询公共场馆信息可以不登录；
         * 查询我的预约、生成预约草稿、生成取消草稿必须登录。
         */
        if (requireLogin && userId == null) {
            return "请先登录后再使用【" + toolName + "】功能。";
        }

        /*
         * 2. 高风险工具拦截。
         *
         * 注意：
         * 真正确认预约、确认取消、退款到账、删除知识库、重建索引等操作
         * 不应该被大模型直接调用。
         *
         * 正确流程应该是：
         * 生成草稿 -> 用户确认 -> 后端 Service 校验 -> 执行真实业务。
         */
        if (requireConfirm || AgentRiskLevel.HIGH.equals(riskLevel)) {
            return "【" + toolName + "】属于高风险操作，不能由 Agent 直接执行，请先生成草稿并由用户确认。";
        }

        /*
         * 3. 工具限流。
         *
         * 防止同一个用户短时间内重复调用同一个工具。
         */
        boolean allowed = rateLimitService.tryAcquire(
                userId,
                toolCode,
                limitSeconds
        );

        if (!allowed) {
            return "你调用【" + toolName + "】过于频繁，请稍后再试。";
        }

        /*
         * 返回 null 表示允许继续执行真实工具逻辑。
         */
        return null;
    }
}
