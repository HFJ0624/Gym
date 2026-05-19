package com.sau.gym.admin.mcp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:hfj
 * 功能:Gym MCP 配置属性
 * 日期: 2026/5/15 21:07
 */
@Data
@Component
@ConfigurationProperties(prefix = "gym.mcp")
public class GymMcpProperties {

    /**
     * 是否启用 MCP。
     * 开发阶段可以通过配置控制开关。
     */
    private Boolean enabled = false;

    /**
     * MCP 传输类型。
     * 第一版支持：
     * stdio：Java 进程启动本地 MCP Server 子进程
     * streamable-http：连接远程 HTTP MCP Server
     */
    private String transportType = "stdio";

    /**
     * stdio 模式下启动 MCP Server 的命令。
     */
    private String command;

    /**
     * stdio 模式下命令参数。
     */
    private List<String> args = new ArrayList<>();

    /**
     * streamable-http 模式下 MCP Server 地址。
     */
    private String url;

    /**
     * 允许暴露给模型的 MCP 工具白名单。
     * 目的：
     * 1. 避免工具太多影响模型选择
     * 2. 避免高风险工具暴露给模型
     */
    private List<String> allowedTools = new ArrayList<>();

    /**
     * 是否记录 MCP 请求日志。
     */
    private Boolean logRequests = false;

    /**
     * 是否记录 MCP 响应日志。
     */
    private Boolean logResponses = false;

    /**
     * 是文件系统 MCP 允许读取的文档根目录。
     */
    private String docsRoot;
}
