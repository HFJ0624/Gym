package com.sau.gym.model.entity.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/16 20:42
 */
@Data
@Schema(description = "AI聊天对话实体类")
public class ChatRecord {

    private Long id;

    private Long userId;      // 用户ID（核心：区分每个用户的记忆）

    private String username;

    private String sessionId;   // 会话ID

    private String userMessage; // 用户消息

    private String aiReply;     // AI回复

    private LocalDateTime createTime;
}
