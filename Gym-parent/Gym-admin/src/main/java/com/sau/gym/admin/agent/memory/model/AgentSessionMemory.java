package com.sau.gym.admin.agent.memory.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者:hfj
 * 功能:Agent 会话记忆对象
 * 作用:
 * 按用户维度保存最近几轮自然语言对话。
 * 日期: 2026/5/27 10:02
 */
@Data
public class AgentSessionMemory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID。
     * 当前先按 userId 维度保存记忆。
     * 如果后续你要支持一个用户同时打开多个聊天窗口，
     * 可以再扩展 sessionId。
     */
    private Long userId;

    /**
     * 会话ID。
     * 当前可以先固定为 default。
     * 后续如果前端传 sessionId，就可以真正按会话隔离。
     */
    private String sessionId;

    /**
     * 最近几条消息。
     * 注意:
     * 这里不是无限保存。
     * Redis 里只保存最近 N 条，避免提示词过长。
     */
    private List<AgentMemoryMessage> recentMessages = new ArrayList<>();

    /**
     * 会话摘要。
     * 当前第一版可以先不用复杂摘要。
     * 后续如果最近消息太多，可以用大模型把旧消息压缩成 summary。
     */
    private String summary;

    /**
     * 最近更新时间。
     */
    private LocalDateTime updatedAt;
}
