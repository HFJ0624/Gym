package com.sau.gym.admin.agent.config;

import com.sau.gym.admin.agent.assistant.GymAgentAssistant;
import com.sau.gym.admin.agent.tool.GymBookingTools;
import com.sau.gym.admin.agent.tool.GymQueryTools;
import com.sau.gym.admin.agent.tool.GymShoppingTools;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * 作者:hfj
 * 功能:大模型+记忆+工具调用形成AI助手
 * 日期: 2026/4/23 14:29
 */
@Configuration
public class LangChain4jAgentConfig {

    /**
     * 创建大模型对象
     * 作用：
     * 1. 告诉 LangChain4j 你要连接哪个模型服务
     * 2. 配置 baseUrl、apiKey、modelName、temperature
     * 这里的 OpenAiChatModel 不代表你一定在用 OpenAI，
     * 只要你的服务是 OpenAI-compatible 接口格式就能接。
     */
    @Bean
    public ChatModel gymChatModel(
            @Value("${gym.ai.base-url}") String baseUrl,
            @Value("${gym.ai.api-key}") String apiKey,
            @Value("${gym.ai.model-name}") String modelName,
            @Value("${gym.ai.temperature:0.1}") Double temperature
    ) {
        return OpenAiChatModel.builder()
                .baseUrl(baseUrl)       // 模型服务基础地址
                .apiKey(apiKey)         // 鉴权密钥
                .modelName(modelName)   // 模型名
                .temperature(temperature) // 温度，越低越稳
                .logRequests(true)      // 打印请求日志，方便调试
                .logResponses(true)     // 打印响应日志，方便调试
                .build();
    }

    /**
     * 创建对话记忆提供器
     * 每个 userId 会对应一个独立的聊天记忆窗口。
     * 这里最多保留最近 20 条消息。
     * 作用：
     * 让模型记得上下文，不然每次都像第一次聊天。
     */
    @Bean
    public ChatMemoryProvider gymChatMemoryProvider() {
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId) // memoryId 通常就是 userId
                .maxMessages(20) //最多保存20条消息
                .build();
    }

    /**
     * 创建 AI 助手对象
     * 这里最关键：
     * 1. 指定大模型
     * 2. 指定会话记忆
     * 3. 指定工具类
     * LangChain4j 会在运行时自动生成 GymAgentAssistant 的实现类。
     */
    @Bean
    public GymAgentAssistant gymAgentAssistant(
            ChatModel gymChatModel,
            ChatMemoryProvider gymChatMemoryProvider,
            GymQueryTools gymQueryTools,
            GymBookingTools gymBookingTools,
            GymShoppingTools gymShoppingTools
    ) {
        return AiServices.builder(GymAgentAssistant.class)
                .chatModel(gymChatModel)                  // 绑定模型
                .chatMemoryProvider(gymChatMemoryProvider) // 绑定会话记忆
                .tools(gymQueryTools, gymBookingTools, gymShoppingTools) // 注册工具
                .build();
    }
}
