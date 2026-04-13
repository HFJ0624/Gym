package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.chat.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatMessageMapper {

    // 插入消息
    int insert(ChatMessage message);

    // 根据会话ID查询历史消息
    List<ChatMessage> selectByConversationId(Long conversationId);

    List<ChatMessage> selectAdminUserChat(Long adminId, Long userId);
}
