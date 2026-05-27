package com.sau.gym.admin.agent.memory.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sau.gym.admin.agent.memory.model.AgentMemoryMessage;
import com.sau.gym.admin.agent.memory.model.AgentSessionMemory;
import com.sau.gym.admin.agent.memory.service.AgentSessionMemoryService;
import com.sau.gym.admin.enums.AgentMemoryRole;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者:hfj
 * 功能:基于 Redis 的 Agent 会话记忆实现
 * 作用:
 * 把用户最近几轮自然语言对话保存到 Redis。
 * 日期: 2026/5/27 10:14
 */
@Service
public class RedisAgentSessionMemoryServiceImpl implements AgentSessionMemoryService {

    /**
     * Redis key 前缀。 key实例:agent:session-memory:user:1001:default
     */
    private static final String KEY_PREFIX = "agent:session-memory:user:";

    /**
     * 默认 sessionId。
     * 当前 AgentChatDto 如果还没有 sessionId 字段，可以先用 default。
     */
    private static final String DEFAULT_SESSION_ID = "default";

    /**
     * 会话记忆有效期。
     * 建议不要太长。
     * 如果用户隔了几天再说“这个场地”，不应该引用几天前的上下文。
     */
    private static final Duration TTL = Duration.ofHours(6);

    /**
     * 最多保存最近多少条消息。
     * 一轮对话包含 2 条:
     * 用户消息 + AI 回复。
     * 这里设置为 10，表示最多保存最近 5 轮对话。
     */
    private static final int MAX_MESSAGE_COUNT = 10;

    /**
     * 单条消息最大长度。
     * 作用:
     * 防止 AI 回复过长导致 Redis 内容过大，也防止拼接给大模型的提示词过长。
     */
    private static final int MAX_MESSAGE_LENGTH = 800;

    private final StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper;

    public RedisAgentSessionMemoryServiceImpl(StringRedisTemplate stringRedisTemplate,
                                              ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 获取用户当前会话记忆。
     */
    @Override
    public AgentSessionMemory getMemory(Long userId, String sessionId) {

        //userId 为空时直接返回空记忆。未登录用户如果也要支持记忆，可以后续按 visitorId 或 IP 保存。
        if (userId == null) {
            return emptyMemory(null, normalizeSessionId(sessionId));
        }

        String realSessionId = normalizeSessionId(sessionId);
        String key = buildKey(userId, realSessionId);

        try {
            String json = stringRedisTemplate.opsForValue().get(key);

            //Redis 中没有历史记忆，则创建空记忆对象。
            if (!StringUtils.hasText(json)) {
                return emptyMemory(userId, realSessionId);
            }

            AgentSessionMemory memory = objectMapper.readValue(json, AgentSessionMemory.class);

            //防御性处理:如果反序列化出来 recentMessages 为空，初始化成空列表，避免后续空指针。
            if (memory.getRecentMessages() == null) {
                memory.setRecentMessages(new ArrayList<>());
            }

            return memory;
        } catch (Exception e) {

            //记忆读取失败不能影响主聊天流程。所以这里打印异常后返回空记忆。
            e.printStackTrace();
            return emptyMemory(userId, realSessionId);
        }
    }

    /**
     * 追加一轮用户和 AI 的对话。
     */
    @Override
    public void appendRound(Long userId, String sessionId, String userMessage, String assistantReply) {

        //用户ID为空时不保存。
        if (userId == null) {
            return;
        }

        //用户消息和 AI 回复都为空，就没有保存意义。
        if (!StringUtils.hasText(userMessage) && !StringUtils.hasText(assistantReply)) {
            return;
        }

        String realSessionId = normalizeSessionId(sessionId);
        AgentSessionMemory memory = getMemory(userId, realSessionId);

        List<AgentMemoryMessage> messages = memory.getRecentMessages();
        if (messages == null) {
            messages = new ArrayList<>();
            memory.setRecentMessages(messages);
        }


        //只保存用户原始消息，不保存 agentInput。
        if (StringUtils.hasText(userMessage)) {
            messages.add(AgentMemoryMessage.user(limitLength(userMessage)));
        }


        //保存 AI 最终回复。
        if (StringUtils.hasText(assistantReply)) {
            messages.add(AgentMemoryMessage.assistant(limitLength(assistantReply)));
        }

        //控制最大消息数。如果超过 MAX_MESSAGE_COUNT，就删除最旧的消息。
        trimMessages(messages);

        //更新基础字段。
        memory.setUserId(userId);
        memory.setSessionId(realSessionId);
        memory.setUpdatedAt(LocalDateTime.now());

        //第一版先做轻量摘要。后续可以升级成 LLM 摘要，把更早的对话压缩成一句话。
        memory.setSummary(buildSimpleSummary(messages));

        //保存回 Redis。
        save(memory);
    }

    /**
     * 构造会话记忆提示词。
     */
    @Override
    public String buildMemoryPrompt(AgentSessionMemory memory) {
        if (memory == null) {
            return "";
        }

        List<AgentMemoryMessage> messages = memory.getRecentMessages();
        if ((messages == null || messages.isEmpty()) && !StringUtils.hasText(memory.getSummary())) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        builder.append("〖最近会话记忆〗\n");
        builder.append("下面是用户最近几轮与体育场馆智能助手的对话，仅用于理解上下文指代。\n");
        builder.append("注意：会话记忆不代表最终业务事实，预约、取消、订单状态必须以后端工具和数据库校验结果为准。\n");

        //如果有摘要，先放摘要。
        if (StringUtils.hasText(memory.getSummary())) {
            builder.append("会话摘要：")
                    .append(memory.getSummary())
                    .append("\n");
        }

        //再放最近消息。
        if (messages != null && !messages.isEmpty()) {
            builder.append("最近消息：\n");

            for (AgentMemoryMessage message : messages) {
                if (message == null || !StringUtils.hasText(message.getContent())) {
                    continue;
                }

                String roleName = getRoleDisplayName(message.getRole());

                builder.append(roleName)
                        .append("：")
                        .append(message.getContent())
                        .append("\n");
            }
        }

        builder.append("如果用户说“刚才”“这个”“那个”“还是一样”，可以参考最近会话记忆理解，但不能编造缺失的业务参数。\n");

        return builder.toString();
    }

    /**
     * 清空会话记忆。
     */
    @Override
    public void clearMemory(Long userId, String sessionId) {
        if (userId == null) {
            return;
        }

        String realSessionId = normalizeSessionId(sessionId);
        stringRedisTemplate.delete(buildKey(userId, realSessionId));
    }

    /**
     * 保存会话记忆到 Redis。
     */
    private void save(AgentSessionMemory memory) {
        if (memory == null || memory.getUserId() == null) {
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(memory);
            stringRedisTemplate.opsForValue().set(
                    buildKey(memory.getUserId(), normalizeSessionId(memory.getSessionId())),
                    json,
                    TTL
            );
        } catch (Exception e) {
            //保存记忆失败不能影响用户聊天。
            e.printStackTrace();
        }
    }

    /**
     * 创建空记忆对象。
     */
    private AgentSessionMemory emptyMemory(Long userId, String sessionId) {
        AgentSessionMemory memory = new AgentSessionMemory();
        memory.setUserId(userId);
        memory.setSessionId(normalizeSessionId(sessionId));
        memory.setRecentMessages(new ArrayList<>());
        memory.setUpdatedAt(LocalDateTime.now());
        return memory;
    }

    /**
     * 构造 Redis key。
     */
    private String buildKey(Long userId, String sessionId) {
        return KEY_PREFIX + userId + ":" + normalizeSessionId(sessionId);
    }

    /**
     * 规范化 sessionId。
     *
     * 当前没有 sessionId 时统一使用 default。
     */
    private String normalizeSessionId(String sessionId) {
        if (!StringUtils.hasText(sessionId)) {
            return DEFAULT_SESSION_ID;
        }
        return sessionId.trim();
    }

    /**
     * 限制单条消息长度。
     */
    private String limitLength(String content) {
        if (!StringUtils.hasText(content)) {
            return "";
        }

        String text = content.trim();

        if (text.length() <= MAX_MESSAGE_LENGTH) {
            return text;
        }

        return text.substring(0, MAX_MESSAGE_LENGTH) + "...";
    }

    /**
     * 裁剪消息列表，只保留最近 MAX_MESSAGE_COUNT 条。
     */
    private void trimMessages(List<AgentMemoryMessage> messages) {
        if (messages == null) {
            return;
        }

        while (messages.size() > MAX_MESSAGE_COUNT) {
            messages.remove(0);
        }
    }

    /**
     * 构造轻量摘要。
     *
     * 第一版不要调用大模型做摘要，避免增加成本和复杂度。
     * 这里简单记录“最近有多少条消息”，并提示系统重点看最近消息。
     *
     * 后续增强:
     * 可以把超过 10 条的旧消息交给大模型压缩成 summary。
     */
    private String buildSimpleSummary(List<AgentMemoryMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return null;
        }

        int userMessageCount = 0;
        int assistantMessageCount = 0;

        for (AgentMemoryMessage message : messages) {
            if (message == null) {
                continue;
            }

            if (AgentMemoryRole.USER.name().equals(message.getRole())) {
                userMessageCount++;
            } else if (AgentMemoryRole.ASSISTANT.name().equals(message.getRole())) {
                assistantMessageCount++;
            }
        }

        return "当前保留最近 " + userMessageCount + " 条用户消息和 "
                + assistantMessageCount + " 条助手回复。";
    }

    /**
     * 获取角色中文名称。
     */
    private String getRoleDisplayName(String role) {
        if (AgentMemoryRole.USER.name().equals(role)) {
            return AgentMemoryRole.USER.getDisplayName();
        }

        if (AgentMemoryRole.ASSISTANT.name().equals(role)) {
            return AgentMemoryRole.ASSISTANT.getDisplayName();
        }

        return "未知角色";
    }
}
