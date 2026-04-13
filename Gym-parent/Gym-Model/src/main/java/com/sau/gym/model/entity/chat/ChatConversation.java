package com.sau.gym.model.entity.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/13 16:06
 */
@Data
@Schema(description = "聊天会话实体类")
public class ChatConversation {
    private Long id;

    private Long userId;

    private String userName;

    private String userAvatar;

    private Long adminId;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
