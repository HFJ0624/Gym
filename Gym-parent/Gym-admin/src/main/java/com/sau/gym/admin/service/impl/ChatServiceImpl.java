package com.sau.gym.admin.service.impl;

import com.sau.gym.admin.mapper.ChatConversationMapper;
import com.sau.gym.admin.mapper.ChatMessageMapper;
import com.sau.gym.admin.service.ChatService;
import com.sau.gym.model.dto.chat.MessageDTO;
import com.sau.gym.model.entity.chat.ChatConversation;
import com.sau.gym.model.entity.chat.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/13 16:05
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatConversationMapper conversationMapper;

    @Autowired
    private ChatMessageMapper messageMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMessage(MessageDTO dto) {
        // 1. 查询会话
        ChatConversation conversation = conversationMapper.selectByUserId(dto.getSenderId());

        // 2. 无会话则创建
        if (conversation == null) {
            conversation = new ChatConversation();
            conversation.setUserId(dto.getSenderId());
            conversation.setUserName(dto.getUserName() == null ? "用户" + dto.getSenderId() : dto.getUserName());
            conversation.setUserAvatar(dto.getUserAvatar());
            conversation.setAdminId(1L);
            conversation.setStatus(1);
            conversationMapper.insert(conversation);
        }

        // 3. 保存消息
        ChatMessage message = new ChatMessage();
        message.setConversationId(conversation.getId());
        message.setSenderType(dto.getSenderType());
        message.setSenderId(dto.getSenderId());
        message.setContent(dto.getContent());
        messageMapper.insert(message);
    }

    @Override
    public List<ChatMessage> getHistory(Long userId) {
        ChatConversation conversation = conversationMapper.selectByUserId(userId);
        if (conversation == null) {
            return new ArrayList<>();
        }
        return messageMapper.selectByConversationId(conversation.getId());
    }

    @Override
    public List<ChatConversation> getAllUserConversations() {
        return conversationMapper.selectAllUserList();
    }

    @Override
    public ChatConversation getConversationByUserId(Long userId) {
        return conversationMapper.selectByUserId(userId);
    }

    @Override
    public void createConversation(ChatConversation conversation) {
        conversationMapper.insert(conversation);
    }
}
