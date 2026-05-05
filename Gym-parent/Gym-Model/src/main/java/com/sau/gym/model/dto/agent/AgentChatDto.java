package com.sau.gym.model.dto.agent;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/5 15:25
 */

import lombok.Data;

/**
 * Agent 聊天请求参数。
 */
@Data
public class AgentChatDto {

    /**
     * 用户消息
     */
    private String message;

    /**
     * 当前场馆ID，可选
     */
    private Long venueId;

    /**
     * 当前场地ID，可选
     */
    private Long courtId;
}
