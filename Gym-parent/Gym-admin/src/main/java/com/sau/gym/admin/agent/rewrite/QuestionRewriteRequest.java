package com.sau.gym.admin.agent.rewrite;

import com.sau.gym.admin.agent.intent.IntentRouteResult;
import com.sau.gym.admin.agent.memory.AgentBusinessContext;
import com.sau.gym.model.dto.agent.AgentChatDto;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:问题重写请求对象
 * 日期: 2026/5/26 13:51
 */
@Data
public class QuestionRewriteRequest {

    /**
     * 当前用户ID。
     * 作用:
     * 目前主要用于 Trace 和后续扩展。
     * 当前规则重写阶段不直接查询用户数据。
     */
    private Long userId;

    /**
     * 用户本轮原始输入。
     * 示例:
     * “那就预约这个”
     * “这个可以退款吗”
     * “明晚七点还有吗”
     */
    private String originalQuestion;

    /**
     * 本轮意图识别结果。
     * 作用:
     * 问题重写时要根据意图决定重写方向。
     */
    private IntentRouteResult intentRouteResult;

    /**
     * 当前有效场馆ID。
     * 来源:
     * 1. 前端本次请求传入的 venueId
     * 2. Redis 业务上下文里的 lastVenueId
     */
    private Long effectiveVenueId;

    /**
     * 当前有效场地ID。
     * 来源:
     * 1. 前端本次请求传入的 courtId
     * 2. Redis 业务上下文里的 lastCourtId
     */
    private Long effectiveCourtId;

    /**
     * Redis 中维护的业务上下文。
     * 当前项目的 AgentBusinessContext 中已经保存了:
     * 最近场馆ID、场馆名称、最近场地ID、场地名称、场地类型、
     * 最近预约日期、开始时间、结束时间、最近意图等信息。
     */
    private AgentBusinessContext businessContext;

    /**
     * 前端本次请求对象。
     * 作用:
     * 可以拿到当前页面传入的 venueId、courtId。
     * 后续如果 AgentChatDto 扩展了 pageType、sessionId，也可以继续用于重写。
     */
    private AgentChatDto agentChatDto;
}
