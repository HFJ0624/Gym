package com.sau.gym.admin.agent.memory.model;

import com.sau.gym.admin.enums.AgentMemoryRole;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 作者:hfj
 * 功能:Agent 会话记忆中的单条消息
 * 作用:
 * 保存一条用户消息或一条 AI 回复。
 * 日期: 2026/5/27 10:02
 */
@Data
public class AgentMemoryMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息角色。
     * 建议保存枚举名称:
     * USER
     * ASSISTANT
     */
    private String role;

    /**
     * 消息内容。
     * 注意:
     * 这里保存的是简化后的自然语言内容。
     * 不要保存完整 agentInput，因为 agentInput 里包含大量系统提示词、上下文和 Trace 信息。
     */
    private String content;

    /**
     * 消息创建时间。
     */
    private LocalDateTime createTime;

    /**
     * 创建用户消息。
     *
     * @param content 用户原始输入
     * @return 会话记忆消息
     */
    public static AgentMemoryMessage user(String content) {
        AgentMemoryMessage message = new AgentMemoryMessage();
        message.setRole(AgentMemoryRole.USER.name());
        message.setContent(content);
        message.setCreateTime(LocalDateTime.now());
        return message;
    }

    /**
     * 创建 AI 助手消息。
     *
     * @param content AI 最终回复
     * @return 会话记忆消息
     */
    public static AgentMemoryMessage assistant(String content) {
        AgentMemoryMessage message = new AgentMemoryMessage();
        message.setRole(AgentMemoryRole.ASSISTANT.name());
        message.setContent(content);
        message.setCreateTime(LocalDateTime.now());
        return message;
    }
}
