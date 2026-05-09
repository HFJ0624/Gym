package com.sau.gym.admin.agent.tool;

import com.sau.gym.admin.agent.service.AgentCancelBookingService;
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

    private final AgentCancelBookingService cancelBookingService;

    public GymCancelBookingTools(AgentCancelBookingService cancelBookingService) {
        this.cancelBookingService = cancelBookingService;
    }

    /**
     * 查询当前用户可取消预约。
     */
    @Tool("查询当前登录用户的可取消预约列表。当用户说“我要取消预约”“我的哪些预约可以取消”“查询可取消预约”时使用。")
    public String queryMyCancelableBookings(@ToolMemoryId Long userId) {
        return cancelBookingService.queryCancelableBookings(userId);
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
        return cancelBookingService.createCancelBookingDraft(userId, bookingId, reason);
    }
}
