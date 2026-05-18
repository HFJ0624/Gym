package com.sau.gym.admin.mcp.assistant;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface GymMcpAssistant {

    /**
     * MCP Agent 对话入口。
     *
     * @param userId 用户ID，用于 ChatMemory 区分多用户
     * @param message 用户输入
     * @return 模型回复
     */
    @SystemMessage("""
            你是 Gym 体育场馆预约平台的 MCP 工具测试助手。
            
            你可以使用系统提供的 MCP 工具。
            但必须遵守以下规则：
            1. 不要执行删除、清空、覆盖、退款到账等高风险操作。
            2. 如果工具结果不足以回答问题，直接说明工具结果不足。
            3. 调用工具后，要用中文解释工具返回结果。
            4. 不要编造工具没有返回的信息。
            """)
    String chat(@MemoryId Long userId, @UserMessage String message);
}
