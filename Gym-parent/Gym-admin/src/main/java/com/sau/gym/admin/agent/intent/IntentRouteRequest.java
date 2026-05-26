package com.sau.gym.admin.agent.intent;

import com.sau.gym.admin.agent.memory.AgentBusinessContext;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:意图识别请求对象
 * 日期: 2026/5/26 10:48
 */
@Data
public class IntentRouteRequest {

    /**
     * 当前登录用户ID。
     * 部分意图需要判断是否为个人操作，例如查询我的预约、取消预约。
     */
    private Long userId;

    /**
     * 用户本轮原始输入。
     * 示例:
     * “帮我预约这个场地明晚7点到8点”
     */
    private String message;

    /**
     * 当前有效场馆ID。
     * 来源优先级一般是:
     * 1. 前端本次请求传入的 venueId
     * 2. Redis 业务上下文里的 lastVenueId
     */
    private Long effectiveVenueId;

    /**
     * 当前有效场地ID。
     * 来源优先级一般是:
     * 1. 前端本次请求传入的 courtId
     * 2. Redis 业务上下文里的 lastCourtId
     */
    private Long effectiveCourtId;

    /**
     * Redis 中维护的业务上下文。
     * 里面可能包含最近场馆、最近场地、最近日期、最近时间段、最近意图等。
     */
    private AgentBusinessContext businessContext;
}
