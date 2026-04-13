package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.chat.ChatConversation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatConversationMapper {

    // 根据用户ID查询会话
    ChatConversation selectByUserId(Long userId);

    // 插入会话
    int insert(ChatConversation conversation);

    // 查询所有用户会话（客服用）
    List<ChatConversation> selectAllUserList();
}
