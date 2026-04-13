package com.sau.gym.model.entity.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/13 16:07
 */
@Data
@Schema(description = "聊天对话实体类")
public class ChatMessage {

    private Long id;

    private Long conversationId;

    private String senderType;

    private Long senderId;

    private String content;

    private Integer isRead;

    private LocalDateTime createdAt;

    private Long receiveUserId; // 接收者ID（用户发给客服时=1，客服发给用户时=用户ID）

}
