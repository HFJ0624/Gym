package com.sau.gym.admin.agent.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sau.gym.admin.agent.memory.AgentBusinessContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
/**
 * 作者:hfj
 * 功能:Agent 业务上下文 Redis 存储器,用户 6 小时不和 Agent 交互，上下文会自动过期。
 * 日期: 2026/5/6 20:09
 */
@Component
public class AgentBusinessContextStore {

    //Redis key 前缀。
    private static final String KEY_PREFIX = "agent:business-context:user:";

    //上下文有效期。时间不要太长，否则用户几天后再问“这个场馆”，可能引用到很早之前的场馆。
    private static final Duration TTL = Duration.ofHours(6);

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public AgentBusinessContextStore(StringRedisTemplate stringRedisTemplate,
                                     ObjectMapper objectMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 获取用户业务上下文。
     *
     * @param userId 用户ID
     * @return 上下文对象；如果不存在，返回 null
     */
    public AgentBusinessContext get(Long userId) {
        if (userId == null) {
            return null;
        }

        try {
            String json = stringRedisTemplate.opsForValue().get(buildKey(userId));
            if (json == null || json.trim().isEmpty()) {
                return null;
            }

            return objectMapper.readValue(json, AgentBusinessContext.class);
        } catch (Exception e) {
            // 上下文读取失败不能影响主聊天流程
            // 所以这里只返回 null，不向上抛异常
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保存用户业务上下文。
     *
     * @param context 上下文对象
     */
    public void save(AgentBusinessContext context) {
        if (context == null || context.getUserId() == null) {
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(context);
            stringRedisTemplate.opsForValue().set(buildKey(context.getUserId()), json, TTL);
        } catch (Exception e) {
            // 保存上下文失败，也不要影响聊天主流程
            e.printStackTrace();
        }
    }

    /**
     * 清除用户业务上下文。
     * 例如用户点击“清空对话”时可以调用。
     *
     * @param userId 用户ID
     */
    public void clear(Long userId) {
        if (userId == null) {
            return;
        }

        stringRedisTemplate.delete(buildKey(userId));
    }

    /**
     * 构建 Redis key。
     */
    private String buildKey(Long userId) {
        return KEY_PREFIX + userId;
    }
}
