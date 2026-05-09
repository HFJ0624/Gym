package com.sau.gym.admin.agent.trace;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 作者:hfj
 * 功能: Agent调用链追踪信息
 * 一次用户请求会生成一个 AgentTraceInfo。
 * 后续工具调用日志都可以读取这里的 userId、userMessage、traceId。
 * 日期: 2026/5/5 20:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentTraceInfo {

    /**
     * 追踪ID。
     * 用于串联一次对话中发生的多个工具调用。
     */
    private String traceId;

    /**
     * 用户ID。
     */
    private Long userId;

    /**
     * 用户原始输入。
     */
    private String userMessage;
}
