package com.sau.gym.admin.agent.service;

import com.sau.gym.admin.enums.AgentRiskLevel;

/**
 * Agent 工具风控服务
 * 说明：
 * 不使用 Spring AOP 拦截 @Tool 方法。
 * 原因：
 * Spring AOP 会把工具类变成 CGLIB 代理对象，
 * LangChain4j 扫描代理对象时可能识别不到 @Tool。
 * 所以这里改成：
 * 在每个 @Tool 方法内部手动调用 checkBeforeToolCall()。
 */
public interface AgentToolGuardService {

    /**
     * 工具调用前检查。
     *
     * @param userId 用户ID
     * @param toolCode 工具编码
     * @param toolName 工具名称
     * @param riskLevel 风险等级
     * @param requireLogin 是否需要登录
     * @param requireConfirm 是否需要用户确认
     * @param limitSeconds 限流秒数
     * @return null 表示允许执行；非 null 表示拒绝原因
     */
    String checkBeforeToolCall(Long userId,
                               String toolCode,
                               String toolName,
                               AgentRiskLevel riskLevel,
                               boolean requireLogin,
                               boolean requireConfirm,
                               int limitSeconds);
}
