package com.sau.gym.admin.service;

import com.sau.gym.model.dto.chat.MessageDTO;
import com.sau.gym.model.entity.chat.ChatConversation;
import com.sau.gym.model.entity.chat.ChatMessage;

import java.util.List;

public interface ChatService {

    void saveMessage(MessageDTO dto);

    List<ChatMessage> getHistory(Long userId);

    List<ChatConversation> getAllUserConversations();

    // 根据用户ID获取会话
    ChatConversation getConversationByUserId(Long userId);


    void createConversation(ChatConversation conversation);
}
