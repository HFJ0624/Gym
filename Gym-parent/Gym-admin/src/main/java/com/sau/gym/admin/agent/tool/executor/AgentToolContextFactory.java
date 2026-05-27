package com.sau.gym.admin.agent.tool.executor;

import com.sau.gym.admin.agent.util.AgentUserContext;
import org.springframework.stereotype.Component;

/**
 * 作者:hfj
 * 功能: Agent 工具上下文构建器
 * 作用:
 * 统一创建 AgentToolExecuteContext，减少每个 @Tool 方法里的重复代码。
 * 日期: 2026/5/27 16:58
 */
@Component
public class AgentToolContextFactory {

    /**
     * 创建一个基础工具执行上下文。
     *
     * @param originalQuestion 用户原始问题
     * @return 工具执行上下文
     */
    public AgentToolExecuteContext createBaseContext(String originalQuestion) {
        AgentToolExecuteContext context = new AgentToolExecuteContext();

        //从 ThreadLocal 中获取当前用户ID。
        context.setUserId(AgentUserContext.getUserId());

        //保存用户原始问题。
        context.setOriginalQuestion(originalQuestion);

        return context;
    }

    /**
     * 创建 RAG 知识库问答工具上下文。
     *
     * @param question 用户问题
     * @param venueId 场馆ID，可为空
     * @param courtId 场地ID，可为空
     * @return 工具执行上下文
     */
    public AgentToolExecuteContext createRagContext(
            String question,
            Long venueId,
            Long courtId
    ) {
        AgentToolExecuteContext context = createBaseContext(question);

        //RAG 工具需要的参数。
        context.addParam("question", question);
        context.addParam("venueId", venueId);
        context.addParam("courtId", courtId);

        return context;
    }

    /**
     * 创建预约草稿工具上下文。
     *
     * @param originalQuestion 用户原始问题
     * @param venueId 场馆ID
     * @param courtId 场地ID
     * @param bookingDate 预约日期
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 工具执行上下文
     */
    public AgentToolExecuteContext createBookingDraftContext(
            String originalQuestion,
            Long venueId,
            Long courtId,
            String bookingDate,
            String startTime,
            String endTime
    ) {
        AgentToolExecuteContext context = createBaseContext(originalQuestion);

        //预约草稿工具需要的参数。
        context.addParam("venueId", venueId);
        context.addParam("courtId", courtId);
        context.addParam("bookingDate", bookingDate);
        context.addParam("startTime", startTime);
        context.addParam("endTime", endTime);

        return context;
    }
}
