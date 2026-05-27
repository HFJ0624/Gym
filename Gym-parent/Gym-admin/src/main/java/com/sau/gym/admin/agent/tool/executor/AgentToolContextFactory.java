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

    /**
     * 创建查询场馆列表工具上下文。
     *
     * @param keyword 场馆关键词，可为空
     * @return 工具执行上下文
     */
    public AgentToolExecuteContext createQueryVenueContext(String keyword) {
        AgentToolExecuteContext context = createBaseContext(keyword);

        // 查询场馆工具需要的参数。
        context.addParam("keyword", keyword);

        return context;
    }

    /**
     * 创建查询公告工具上下文。
     *
     * @param originalQuestion 用户原始问题，可为空
     * @return 工具执行上下文
     */
    public AgentToolExecuteContext createQueryNoticeContext(String originalQuestion) {
        AgentToolExecuteContext context = createBaseContext(originalQuestion);

        // 查询公告暂时不需要额外参数。
        // 这里保留 context，方便后续增加 pageSize、keyword 等参数。

        return context;
    }

    /**
     * 创建查询当前用户可取消预约工具上下文。
     *
     * @param originalQuestion 用户原始问题
     * @return 工具执行上下文
     */
    public AgentToolExecuteContext createCancelableBookingContext(String originalQuestion) {
        AgentToolExecuteContext context = createBaseContext(originalQuestion);
        return context;
    }

    /**
     * 创建取消预约草稿工具上下文。
     *
     * @param originalQuestion 用户原始问题
     * @param bookingId 要取消的预约ID
     * @param reason 取消原因
     * @return 工具执行上下文
     */
    public AgentToolExecuteContext createCancelBookingDraftContext(
            String originalQuestion,
            Long bookingId,
            String reason
    ) {
        AgentToolExecuteContext context = createBaseContext(originalQuestion);

        // 取消预约草稿工具需要的参数。
        context.addParam("bookingId", bookingId);
        context.addParam("reason", reason);

        return context;
    }

    /**
     * 创建商品下单草稿工具上下文。
     *
     * @param originalQuestion 用户原始问题
     * @param productName 商品名称
     * @param quantity 商品数量
     * @return 工具执行上下文
     */
    public AgentToolExecuteContext createShoppingDraftContext(
            String originalQuestion,
            String productName,
            Integer quantity
    ) {
        AgentToolExecuteContext context = createBaseContext(originalQuestion);

        // 商品下单草稿工具需要的参数。
        context.addParam("productName", productName);
        context.addParam("quantity", quantity);

        return context;
    }
}
