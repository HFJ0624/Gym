package com.sau.gym.admin.mcp.assistant;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * MCP 文件写入草稿内容生成助手
 *
 * 作用：
 * 当管理员只给出提示词，没有给出完整文件内容时，
 * 由这个 Assistant 生成一份待写入内容。
 */
public interface GymMcpFileDraftAssistant {

    /**
     * 根据管理员需求生成文件内容。
     *
     * @param message 管理员需求
     * @return 生成后的文件内容
     */
    @SystemMessage("""
            你是 Gym 后台文档草稿生成助手。

            你的任务：
            根据管理员的要求，生成适合写入 Markdown、TXT、YAML、SQL 等文件的内容。

            规则：
            1. 只输出文件正文内容。
            2. 不要输出解释性开场白。
            3. 不要输出“好的，以下是”。
            4. 如果是 Markdown 文件，使用清晰标题和小节。
            5. 内容要适合后续导入 RAG 知识库。
            6. 不要编造具体价格、开放时间、地址，除非管理员明确提供。
            7. 内容控制在 3000 字以内。
            """)
    String generateContent(@UserMessage String message);
}
