package com.sau.gym.admin.agent.util;

/**
 * 作者:hfj
 * 功能:Agent 当前用户上下文
 * 作用:
 * 在一次 Agent 对话过程中，把当前登录用户ID保存到 ThreadLocal 中。
 * 为什么需要这个类:
 * LangChain4j 调用 @Tool 方法时，不一定会把 userId 自动传进去。
 * 但是工具风控、工具限流、工具日志都需要知道当前是谁在调用工具。
 * 所以我们在 AgentServiceImpl 调用大模型之前，把 userId 放进 ThreadLocal。
 * 后续 GymRagTools、GymBookingTools 等工具类就可以从这里取到当前用户ID。
 * 注意:
 * ThreadLocal 用完必须 clear，否则在 Web 线程复用时可能出现用户串号问题。
 * 日期: 2026/5/26 11:28
 */
public class AgentUserContext {

    /**
     * 当前线程绑定的用户ID。
     */
    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();

    /**
     * 设置当前用户ID。
     *
     * @param userId 当前登录用户ID
     */
    public static void setUserId(Long userId) {
        USER_ID_HOLDER.set(userId);
    }

    /**
     * 获取当前用户ID。
     *
     * @return 当前线程中的用户ID，可能为空
     */
    public static Long getUserId() {
        return USER_ID_HOLDER.get();
    }

    /**
     * 清理当前线程中的用户ID。
     *
     * 这个方法必须在 finally 中调用。
     */
    public static void clear() {
        USER_ID_HOLDER.remove();
    }
}
