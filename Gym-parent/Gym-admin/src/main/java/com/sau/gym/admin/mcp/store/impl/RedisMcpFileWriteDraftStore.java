package com.sau.gym.admin.mcp.store.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sau.gym.admin.mcp.model.McpFileWriteDraft;
import com.sau.gym.admin.mcp.store.McpFileWriteDraftStore;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 作者:hfj
 * 功能:Redis MCP 文件写入草稿存储
 * 日期: 2026/5/19 10:09
 */
@Component
public class RedisMcpFileWriteDraftStore implements McpFileWriteDraftStore {

    private final StringRedisTemplate stringRedisTemplate;

    private final ObjectMapper objectMapper;

    public RedisMcpFileWriteDraftStore(StringRedisTemplate stringRedisTemplate,
                                       ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void save(Long userId, McpFileWriteDraft draft) {
        try {
            String json = objectMapper.writeValueAsString(draft);

            /*
             * 草稿 10 分钟有效。
             */
            stringRedisTemplate.opsForValue()
                    .set(buildKey(userId), json, Duration.ofMinutes(10));
        } catch (Exception e) {
            throw new RuntimeException("保存 MCP 文件写入草稿失败", e);
        }
    }

    @Override
    public McpFileWriteDraft get(Long userId) {
        try {
            String json = stringRedisTemplate.opsForValue().get(buildKey(userId));

            if (json == null || json.trim().isEmpty()) {
                return null;
            }

            return objectMapper.readValue(json, McpFileWriteDraft.class);
        } catch (Exception e) {
            throw new RuntimeException("读取 MCP 文件写入草稿失败", e);
        }
    }

    @Override
    public void clear(Long userId) {
        stringRedisTemplate.delete(buildKey(userId));
    }

    private String buildKey(Long userId) {
        return "mcp:file-write:draft:" + userId;
    }
}
