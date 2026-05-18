package com.sau.gym.admin.mcp.service;

public interface GymMcpChatService {

    /**
     * 调用 MCP Agent。
     *
     * @param userId 用户ID
     * @param message 用户消息
     * @return 回复
     */
    String chat(Long userId, String message);
}
