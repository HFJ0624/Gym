package com.sau.gym.admin.agent.tool.executor;

import com.sau.gym.admin.enums.AgentRiskLevel;

import java.util.List;

public interface GymAgentToolExecutor {

    /**
     * 工具编码。
     *
     * 要求:
     * 1. 全局唯一。
     * 2. 使用英文小写 + 下划线。
     *
     * 示例:
     * ask_gym_knowledge
     * create_booking_draft
     * confirm_booking
     * cancel_booking
     * query_available_slot
     */
    String toolCode();

    /**
     * 工具名称。
     *
     * 给后台管理页面和 Trace 日志展示。
     *
     * 示例:
     * RAG知识库问答
     * 生成预约草稿
     */
    String toolName();

    /**
     * 工具描述。
     *
     * 给大模型和后台页面看。
     *
     * 示例:
     * “适合回答预约规则、退款规则、停车说明、开放时间等知识类问题。”
     */
    String description();

    /**
     * 工具风险等级。
     *
     * LOW:
     * 查询类工具，例如 RAG 问答、场馆查询。
     *
     * MEDIUM:
     * 生成草稿类工具，例如预约草稿。
     *
     * HIGH:
     * 会真实改变业务状态的工具，例如确认预约、取消预约。
     */
    AgentRiskLevel riskLevel();

    /**
     * 是否需要登录。
     *
     * 示例:
     * 查询公告、规则可以不登录。
     * 查询我的预约、生成预约草稿、取消预约必须登录。
     */
    boolean needLogin();

    /**
     * 是否需要用户确认。
     *
     * 示例:
     * 取消预约、确认预约需要用户确认。
     * RAG 问答不需要确认。
     */
    boolean needConfirm();

    /**
     * 限流秒数。
     *
     * 示例:
     * 0 表示不限流。
     * 3 表示同一用户 3 秒内只能调用一次。
     */
    int rateLimitSeconds();

    /**
     * 参数定义。
     *
     * 作用:
     * 1. 给大模型看这个工具需要什么参数。
     * 2. 给后续后台管理页面展示。
     * 3. 给统一参数校验使用。
     */
    List<AgentToolParamDefinition> paramDefinitions();

    /**
     * 执行工具。
     *
     * @param context 工具执行上下文
     * @return 工具统一执行结果
     */
    AgentToolExecuteResult execute(AgentToolExecuteContext context);
}
