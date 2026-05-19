package com.sau.gym.admin.mcp.config;

import com.sau.gym.admin.mcp.assistant.GymMcpAssistant;
import com.sau.gym.admin.mcp.assistant.GymMcpFileDraftAssistant;
import com.sau.gym.admin.mcp.assistant.GymMcpWriteAssistant;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.StreamableHttpMcpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:hfj
 * 功能: Gym MCP配置类
 * 日期: 2026/5/15 21:10
 */
@Configuration
@ConditionalOnProperty(prefix = "gym.mcp", name = "enabled", havingValue = "true")
public class GymMcpConfig {

    /**
     * 创建 MCP Transport。
     * MCP Transport 负责客户端和 MCP Server 之间的通信。
     */
    @Bean
    public McpTransport gymMcpTransport(GymMcpProperties properties) {
        String transportType = properties.getTransportType();

        if ("streamable-http".equalsIgnoreCase(transportType)) {
            //Streamable HTTP 模式：连接一个已经启动的 MCP HTTP Server。
            return StreamableHttpMcpTransport.builder()
                    .url(properties.getUrl())
                    .logRequests(Boolean.TRUE.equals(properties.getLogRequests()))
                    .logResponses(Boolean.TRUE.equals(properties.getLogResponses()))
                    .build();
        }

        //stdio模式：由Java应用启动MCP Server子进程
        List<String> command = new ArrayList<>();
        command.add(properties.getCommand());
        command.addAll(properties.getArgs());

        return StdioMcpTransport.builder()
                .command(command)
                .logEvents(Boolean.TRUE.equals(properties.getLogRequests()))
                .build();
    }

    /**
     * 创建 MCP Client。
     * McpClient 负责：
     * 1. 初始化连接
     * 2. 发现工具
     * 3. 执行工具调用
     */
    @Bean(destroyMethod = "close")
    public McpClient gymMcpClient(McpTransport gymMcpTransport) {
        return DefaultMcpClient.builder()
                .key("gym-mcp-client")
                .transport(gymMcpTransport)
                .build();
    }

    /**
     * 创建 MCP ToolProvider。
     * 必须做工具白名单过滤。
     */
    @Bean("mcpReadToolProvider")
    public ToolProvider gymMcpToolProvider(McpClient gymMcpClient,
                                           GymMcpProperties properties) {
        McpToolProvider.Builder builder = McpToolProvider.builder()
                .mcpClients(gymMcpClient);

        if (properties.getAllowedTools() != null && !properties.getAllowedTools().isEmpty()) {
            builder.filterToolNames(properties.getAllowedTools().toArray(new String[0]));
        }

        return builder.build();
    }

    /**
     * 写入 MCP ToolProvider。
     */
    @Bean("mcpWriteToolProvider")
    public ToolProvider mcpWriteToolProvider(McpClient gymMcpClient,
                                             GymMcpProperties properties) {
        McpToolProvider.Builder builder = McpToolProvider.builder()
                .mcpClients(gymMcpClient);

        if (properties.getWriteTools() != null && !properties.getWriteTools().isEmpty()) {
            builder.filterToolNames(properties.getWriteTools().toArray(new String[0]));
        }

        return builder.build();
    }

    /**
     * 创建后台 MCP 文件文档助手。
     * 只绑定只读 MCP 工具。
     */
    @Bean
    public GymMcpAssistant gymMcpAssistant(ChatModel gymChatModel,
                                           ChatMemoryProvider gymChatMemoryProvider,
                                           @Qualifier("mcpReadToolProvider")ToolProvider gymMcpToolProvider) {
        return AiServices.builder(GymMcpAssistant.class)
                .chatModel(gymChatModel)
                .chatMemoryProvider(gymChatMemoryProvider)
                .toolProvider(gymMcpToolProvider)
                .build();
    }

    /**
     * 创建 MCP 文件写入助手。
     */
    @Bean
    public GymMcpWriteAssistant gymMcpWriteAssistant(ChatModel gymChatModel,
                                                     ChatMemoryProvider gymChatMemoryProvider,
                                                     @Qualifier("mcpWriteToolProvider") ToolProvider mcpWriteToolProvider) {
        return AiServices.builder(GymMcpWriteAssistant.class)
                .chatModel(gymChatModel)
                .chatMemoryProvider(gymChatMemoryProvider)
                .toolProvider(mcpWriteToolProvider)
                .build();
    }

    /**
     * 创建文件内容草稿生成助手。
     * 这个助手不绑定 MCP 工具。
     * 只负责根据管理员提示词生成文件内容。
     */
    @Bean
    public GymMcpFileDraftAssistant gymMcpFileDraftAssistant(ChatModel gymChatModel) {
        return AiServices.builder(GymMcpFileDraftAssistant.class)
                .chatModel(gymChatModel)
                .build();
    }
}
