package com.sau.gym.admin.enums;

/**
 * 作者: hfj
 * 功能: Agent 会话记忆消息角色枚举
 * 作用:
 * 用来区分一条会话记忆是用户说的，还是 AI 助手回复的。
 */
public enum AgentMemoryRole {

    /**
     * 用户消息。
     */
    USER("用户"),

    /**
     * AI 助手回复。
     */
    ASSISTANT("助手");

    /**
     * 中文显示名称。
     */
    private final String displayName;

    AgentMemoryRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
