package com.sau.gym.admin.agent.tool;

import com.alibaba.fastjson.JSON;
import com.sau.gym.admin.agent.service.AgentCancelBookingService;
import com.sau.gym.admin.agent.service.AgentToolGuardService;
import com.sau.gym.admin.agent.tool.executor.AgentToolContextFactory;
import com.sau.gym.admin.agent.tool.executor.AgentToolExecuteContext;
import com.sau.gym.admin.agent.tool.executor.AgentToolExecuteResult;
import com.sau.gym.admin.agent.tool.registry.GymAgentToolRegistry;
import com.sau.gym.admin.enums.AgentRiskLevel;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import org.springframework.stereotype.Component;

/**
 * 作者:hfj
 * 功能:Agent取消预约工具
 * 日期: 2026/5/9 10:45
 */
@Component
public class GymCancelBookingTools {

    private final GymAgentToolRegistry gymAgentToolRegistry;
    private final AgentToolContextFactory agentToolContextFactory;

    public GymCancelBookingTools(
            GymAgentToolRegistry gymAgentToolRegistry,
            AgentToolContextFactory agentToolContextFactory
    ) {
        this.gymAgentToolRegistry = gymAgentToolRegistry;
        this.agentToolContextFactory = agentToolContextFactory;
    }

    /**
     * 查询当前用户可取消预约。
     */
    @Tool("查询当前登录用户的可取消预约列表。当用户说“我要取消预约”“我的哪些预约可以取消”“查询可取消预约”时使用。")
    public String queryMyCancelableBookings(@ToolMemoryId Long userId) {
        // 1. 构造统一工具执行上下文。
        AgentToolExecuteContext context = agentToolContextFactory.createCancelableBookingContext("查询可取消预约");

        // 2. 通过工具注册器执行工具。
        AgentToolExecuteResult result = gymAgentToolRegistry.execute(
                AgentToolCodes.QUERY_CANCELABLE_BOOKING,
                context
        );

        // 3. 返回统一 JSON 给大模型。
        return JSON.toJSONString(result);
    }

    /**
     * 生成取消预约草稿。
     * 注意：
     * 这个方法不会真正取消预约，只会生成取消草稿。
     */
    @Tool("根据预约ID生成取消预约草稿。当用户说“取消预约 12”“帮我取消订单12”时使用。不会真正取消预约。")
    public String createCancelBookingDraft(
            @P("要取消的预约ID，例如 12") Long bookingId,
            @P("取消原因，如果用户没有说明，可以填：用户通过Agent取消预约") String reason,
            @ToolMemoryId Long userId
    ) {
        // 1. 构造统一工具执行上下文。
        AgentToolExecuteContext context = agentToolContextFactory.createCancelBookingDraftContext(
                "创建取消预约草稿",
                bookingId,
                reason
        );

        // 2. 通过工具注册器执行工具。
        AgentToolExecuteResult result = gymAgentToolRegistry.execute(
                AgentToolCodes.CREATE_CANCEL_BOOKING_DRAFT,
                context
        );

        // 3. 返回统一 JSON 给大模型。
        return JSON.toJSONString(result);
    }
}
