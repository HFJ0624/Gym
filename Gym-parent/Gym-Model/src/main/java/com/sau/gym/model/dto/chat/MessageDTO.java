package com.sau.gym.model.dto.chat;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/13 16:02
 */
@Data
public class MessageDTO {
    private Long conversationId;

    private String senderType;    // user / admin

    private Long senderId;

    private Long receiveUserId;   // 客服发给谁

    private String content;

    private String userName;      // 用户名

    private String userAvatar;    // 用户头像

    private LocalDateTime sendTime;
}
