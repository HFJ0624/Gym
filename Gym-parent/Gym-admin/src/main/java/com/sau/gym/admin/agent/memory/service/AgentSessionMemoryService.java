package com.sau.gym.admin.agent.memory.service;

import com.sau.gym.admin.agent.memory.model.AgentSessionMemory;

public interface AgentSessionMemoryService {

    /**
     * 获取用户当前会话记忆。
     *
     * @param userId 用户ID
     * @param sessionId 会话ID，可为空
     * @return 会话记忆；不存在时返回空记忆对象
     */
    AgentSessionMemory getMemory(Long userId, String sessionId);

    /**
     * 追加一轮对话。
     *
     * 一轮对话包含:
     * 1. 用户原始输入
     * 2. AI 最终回复
     *
     * @param userId 用户ID
     * @param sessionId 会话ID，可为空
     * @param userMessage 用户原始消息
     * @param assistantReply AI 最终回复
     */
    void appendRound(Long userId, String sessionId, String userMessage, String assistantReply);

    /**
     * 构造会话记忆提示词。
     *
     * 这个提示词会被拼接到 agentInput 中，传给大模型。
     *
     * @param memory 会话记忆对象
     * @return 会话记忆提示词
     */
    String buildMemoryPrompt(AgentSessionMemory memory);

    /**
     * 清空用户当前会话记忆。
     *
     * @param userId 用户ID
     * @param sessionId 会话ID，可为空
     */
    void clearMemory(Long userId, String sessionId);
}
