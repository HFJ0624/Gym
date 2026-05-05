package com.sau.gym.admin.agent.context;

/**
 * 作者:hfj
 * 功能:Agent调用链上下文
 * 使用 ThreadLocal 保存一次请求内的追踪信息。
 * 日期: 2026/5/5 20:28
 */
public class AgentTraceContext {

    private static final ThreadLocal<AgentTraceInfo> HOLDER = new ThreadLocal<>();

    private AgentTraceContext() {
    }

    /**
     * 设置当前请求的追踪信息。
     */
    public static void set(AgentTraceInfo traceInfo) {
        HOLDER.set(traceInfo);
    }

    /**
     * 获取当前请求的追踪信息。
     */
    public static AgentTraceInfo get() {
        return HOLDER.get();
    }

    /**
     * 清除当前请求的追踪信息。
     */
    public static void clear() {
        HOLDER.remove();
    }
}
