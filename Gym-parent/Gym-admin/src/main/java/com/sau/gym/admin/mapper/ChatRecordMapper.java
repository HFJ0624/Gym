package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.chat.ChatRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ChatRecordMapper {

    // 1. 保存聊天记录
    int insertChatRecord(ChatRecord record);

    // 2. 查询用户最近N条对话（用于上下文记忆）
    List<ChatRecord> selectUserRecentHistory(Long userId, int limit);
}
