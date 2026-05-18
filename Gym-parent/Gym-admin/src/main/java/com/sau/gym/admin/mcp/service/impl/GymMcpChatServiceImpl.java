package com.sau.gym.admin.mcp.service.impl;

import com.sau.gym.admin.agent.trace.AgentTraceContext;
import com.sau.gym.admin.agent.trace.AgentTraceInfo;
import com.sau.gym.admin.mcp.assistant.GymMcpAssistant;
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

    @Autowired
    private GymMcpAssistant gymMcpAssistant;

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
            return gymMcpAssistant.chat(userId, finalMessage);

        } catch (Exception e) {
            //调用失败也进行记录
            AgentTraceContext.set(new AgentTraceInfo(traceId, userId, "MCP 工具调用失败"));
            return "MCP 工具调用失败：" + e.getMessage();
        } finally {
            //清理ThreadLocal
            AgentTraceContext.clear();
        }
    }
}
