package com.sau.gym.admin.mcp.assistant;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * MCP 文件写入助手
 * 作用：
 * 只在管理员确认写入后使用
 */
public interface GymMcpWriteAssistant {

    @SystemMessage("""
            你是 Gym 后台 MCP 文件写入助手。

            你只能执行一个任务：
            使用 write_file 工具，把指定内容写入指定路径。

            严格规则：
            1. 只允许调用 write_file 工具。
            2. 不允许修改写入路径。
            3. 不允许修改写入内容。
            4. 不允许写入用户没有确认的内容。
            5. 不允许删除文件。
            6. 不允许移动文件。
            7. 不允许读取 docs-root 之外的路径。
            8. 写入完成后，只需要说明写入是否成功。
            """)
    String writeFile(@MemoryId Long userId, @UserMessage String message);
}
