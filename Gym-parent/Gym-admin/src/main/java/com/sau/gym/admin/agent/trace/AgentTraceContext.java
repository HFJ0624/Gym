package com.sau.gym.admin.agent.trace;

/**
 * 作者:hfj
 * 功能:Agent Trace 上下文
 * 作用：
 * 使用 ThreadLocal 保存当前请求的 traceId 和步骤序号。
 * 日期: 2026/5/9 14:40
 */
public class AgentTraceContext {

    /**
     * 当前请求的 Trace 信息。
     */
    private static final ThreadLocal<AgentTraceInfo> HOLDER = new ThreadLocal<>();

    /**
     * 当前请求的步骤序号。
     *
     * 用于 agent_trace_step.sort_order。
     */
    private static final ThreadLocal<Integer> STEP_ORDER_HOLDER = ThreadLocal.withInitial(() -> 0);

    private AgentTraceContext() {
    }

    /**
     * 设置当前请求的追踪信息。
     */
    public static void set(AgentTraceInfo traceInfo) {
        HOLDER.set(traceInfo);
        STEP_ORDER_HOLDER.set(0);
    }

    /**
     * 获取当前请求的追踪信息。
     */
    public static AgentTraceInfo get() {
        return HOLDER.get();
    }

    /**
     * 获取当前 traceId。
     *
     * AOP 记录工具日志时可以直接用这个方法。
     */
    public static String getTraceId() {
        AgentTraceInfo info = HOLDER.get();
        return info == null ? null : info.getTraceId();
    }

    /**
     * 获取当前用户ID。
     */
    public static Long getUserId() {
        AgentTraceInfo info = HOLDER.get();
        return info == null ? null : info.getUserId();
    }

    /**
     * 获取当前用户消息。
     */
    public static String getMessage() {
        AgentTraceInfo info = HOLDER.get();
        return info == null ? null : info.getUserMessage();
    }

    /**
     * 获取下一个步骤序号。
     */
    public static int nextOrder() {
        Integer current = STEP_ORDER_HOLDER.get();

        if (current == null) {
            current = 0;
        }

        int next = current + 1;
        STEP_ORDER_HOLDER.set(next);

        return next;
    }

    /**
     * 清除当前请求的追踪信息。
     *
     * 必须在 finally 中调用，防止线程复用导致 traceId 串号。
     */
    public static void clear() {
        HOLDER.remove();
        STEP_ORDER_HOLDER.remove();
    }
}
