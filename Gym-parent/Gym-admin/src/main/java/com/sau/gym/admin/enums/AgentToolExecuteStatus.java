package com.sau.gym.admin.enums;

/**
 * 作者: hfj
 * 功能: Agent 工具执行状态枚举
 * 作用:
 * 用统一枚举描述工具执行结果。
 */
public enum AgentToolExecuteStatus {

    /**
     * 执行成功。
     */
    SUCCESS,

    /**
     * 参数不合法。
     */
    PARAM_ERROR,

    /**
     * 未登录或无权限。
     */
    PERMISSION_DENIED,

    /**
     * 工具被限流。
     */
    RATE_LIMITED,

    /**
     * 需要用户确认。
     */
    NEED_CONFIRM,

    /**
     * 工具执行失败。
     */
    FAILED,

    /**
     * 系统异常。
     */
    ERROR
}
