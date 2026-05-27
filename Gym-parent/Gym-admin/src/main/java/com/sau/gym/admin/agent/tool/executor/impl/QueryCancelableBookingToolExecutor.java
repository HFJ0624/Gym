package com.sau.gym.admin.agent.tool.executor.impl;

import com.sau.gym.admin.agent.service.AgentCancelBookingService;
import com.sau.gym.admin.agent.service.AgentToolGuardService;
import com.sau.gym.admin.agent.tool.AgentToolCodes;
import com.sau.gym.admin.agent.tool.executor.AbstractGymAgentToolExecutor;
import com.sau.gym.admin.agent.tool.executor.AgentToolExecuteContext;
import com.sau.gym.admin.agent.tool.executor.AgentToolExecuteResult;
import com.sau.gym.admin.enums.AgentRiskLevel;
import org.springframework.stereotype.Component;

/**
 * 作者:hfj
 * 功能:查询当前用户可取消预约工具执行器
 * 作用:
 * 1. 查询当前登录用户可以取消的预约。
 * 2. 不会真正取消预约。
 * 3. 查询我的预约需要登录。
 * 日期: 2026/5/27 20:46
 */
@Component
public class QueryCancelableBookingToolExecutor extends AbstractGymAgentToolExecutor {

    private final AgentCancelBookingService cancelBookingService;

    public QueryCancelableBookingToolExecutor(
            AgentToolGuardService agentToolGuardService,
            AgentCancelBookingService cancelBookingService
    ) {
        super(agentToolGuardService);
        this.cancelBookingService = cancelBookingService;
    }

    @Override
    public String toolCode() {
        return AgentToolCodes.QUERY_CANCELABLE_BOOKING;
    }

    @Override
    public String toolName() {
        return "查询可取消预约";
    }

    @Override
    public String description() {
        return "查询当前登录用户可以取消的预约列表。适合用户说我要取消预约、我的哪些预约可以取消。";
    }

    @Override
    public AgentRiskLevel riskLevel() {
        return AgentRiskLevel.LOW;
    }

    @Override
    public boolean needLogin() {
        return true;
    }

    @Override
    public boolean needConfirm() {
        return false;
    }

    @Override
    public int rateLimitSeconds() {
        return 3;
    }

    @Override
    protected AgentToolExecuteResult doExecute(AgentToolExecuteContext context) {
        Long userId = context.getUserId();

        String text = cancelBookingService.queryCancelableBookings(userId);

        return AgentToolExecuteResult.success(
                "可取消预约查询完成。",
                text
        );
    }
}
