package com.sau.gym.admin.agent.security;

import com.sau.gym.admin.enums.AgentRiskLevel;

import java.lang.annotation.*;

/**
 * Agent 工具风控注解
 * 用法：
 * 在 LangChain4j @Tool 方法上同时加这个注解。
 * 作用：
 * 1. 标记工具风险等级
 * 2. 标记是否需要登录
 * 3. 标记是否需要用户确认
 * 4. 设置工具调用频率限制
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AgentToolGuard {

    /**
     * 工具编码。
     * 要求全项目唯一。
     * 示例：
     * query_venue
     * create_booking_draft
     * create_cancel_booking_draft
     */
    String toolCode();

    /**
     * 工具名称。
     */
    String toolName();

    /**
     * 风险等级。
     */
    AgentRiskLevel riskLevel() default AgentRiskLevel.LOW;

    /**
     * 是否要求登录。
     * 大多数业务工具都应该要求登录。
     */
    boolean requireLogin() default true;

    /**
     * 是否需要用户确认。
     * 注意：
     * 第一版中，如果一个工具被标记为 requireConfirm = true，
     * AOP 会直接拒绝模型调用。
     * 因为真正的确认动作应该在 AgentServiceImpl.handlePendingAction() 里处理，
     * 不能让模型直接通过工具执行。
     */
    boolean requireConfirm() default false;

    /**
     * 同一个用户调用该工具的限流秒数。
     * 例如：
     * limitSeconds = 3 表示同一个用户 3 秒内不能重复调用同一个工具。
     * 0 表示不限流。
     */
    int limitSeconds() default 0;
}
