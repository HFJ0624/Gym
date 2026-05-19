package com.sau.gym.admin.mcp.service.impl;

import com.sau.gym.admin.agent.trace.AgentTraceContext;
import com.sau.gym.admin.agent.trace.AgentTraceInfo;
import com.sau.gym.admin.mcp.assistant.GymMcpAssistant;
import com.sau.gym.admin.mcp.config.GymMcpProperties;
import com.sau.gym.admin.mcp.service.GymMcpChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/15 21:17
 */
@Service
public class GymMcpChatServiceImpl implements GymMcpChatService {

    private final GymMcpAssistant gymMcpAssistant;

    private final GymMcpProperties mcpProperties;

    public GymMcpChatServiceImpl(GymMcpAssistant gymMcpAssistant,
                                 GymMcpProperties mcpProperties) {
        this.gymMcpAssistant = gymMcpAssistant;
        this.mcpProperties = mcpProperties;
    }

    @Override
    public String chat(Long userId, String message) {
        if (message == null || message.trim().isEmpty()) {
            return "消息不能为空。";
        }

        String finalMessage = message.trim();

        //MCP调用也生成traceId。
        String traceId = UUID.randomUUID().toString().replace("-", "");

        try {
            //保存MCP调用链路信息
            AgentTraceContext.set(new AgentTraceInfo(traceId, userId, finalMessage));

            //构造文件系统 MCP 专用输入。这里把允许读取的文档根目录告诉模型。
            String enhancedMessage = buildFileSystemDocMessage(finalMessage);

            //调用MCP Assistant
            return gymMcpAssistant.chat(userId, enhancedMessage);

        } catch (Exception e) {
            //调用失败也进行记录
            AgentTraceContext.set(new AgentTraceInfo(traceId, userId, "MCP 工具调用失败"));
            return "MCP 工具调用失败：" + e.getMessage();
        } finally {
            //清理ThreadLocal
            AgentTraceContext.clear();
        }
    }

    /**
     * 构造文件系统文档助手输入。
     *
     * 作用：
     * 1. 告诉 Agent 当前允许读取的文档根目录
     * 2. 强调只能只读
     * 3. 要求回答时说明参考文件
     */
    private String buildFileSystemDocMessage(String userMessage) {
        String docsRoot = mcpProperties.getDocsRoot();

        return """
                你现在是 Gym 后台文件系统 MCP 文档助手。
                  
                  当前允许读取的文档根目录：
                  %s
      
                  你只能读取该目录下的文档。
      
                  重要限制：
                  1. 如果用户只是问有哪些文件，只使用 list_directory 查看一级文件列表。
                  2. 不要读取整个目录树。
                  3. 不要一次读取多个文件。
                  4. 每次最多读取 1 个最相关文件。
                  5. 不要输出文件原文，只做摘要、归纳和解释。
                  6. 如果文件内容很长，只总结关键内容。
                  7. 单次回答控制在 800 字以内。
                  8. 如果当前文档中没有相关信息，直接说明没有找到，不要编造。
      
                  允许分析的文档类型：
                  1. Markdown 文件，例如 .md
                  2. 文本文件，例如 .txt
                  3. YAML 配置文件，例如 .yml / .yaml
                  4. SQL 文件，例如 .sql
      
                  禁止操作：
                  1. 不允许写文件。
                  2. 不允许修改文件。
                  3. 不允许删除文件。
                  4. 不允许移动文件。
                  5. 不允许读取该目录之外的路径。
      
                  管理员问题：
                  %s
                """.formatted(docsRoot, userMessage);
    }
}
