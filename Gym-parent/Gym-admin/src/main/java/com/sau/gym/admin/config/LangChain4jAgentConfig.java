package com.sau.gym.admin.config;

import com.sau.gym.admin.agent.GymAgentAssistant;
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
 * 功能:
 * 日期: 2026/4/23 14:29
 */
@Configuration
public class LangChain4jAgentConfig {

    @Bean
    public ChatModel gymChatModel(
            @Value("${gym.ai.base-url}") String baseUrl,
            @Value("${gym.ai.api-key}") String apiKey,
            @Value("${gym.ai.model-name}") String modelName,
            @Value("${gym.ai.temperature:0.1}") Double temperature
    ) {
        return OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(modelName)
                .temperature(temperature)
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    @Bean
    public ChatMemoryProvider gymChatMemoryProvider() {
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(20)
                .build();
    }

    @Bean
    public GymAgentAssistant gymAgentAssistant(
            ChatModel gymChatModel,
            ChatMemoryProvider gymChatMemoryProvider,
            GymQueryTools gymQueryTools,
            GymBookingTools gymBookingTools,
            GymShoppingTools gymShoppingTools
    ) {
        return AiServices.builder(GymAgentAssistant.class)
                .chatModel(gymChatModel)
                .chatMemoryProvider(gymChatMemoryProvider)
                .tools(gymQueryTools, gymBookingTools, gymShoppingTools)
                .build();
    }
}
