package com.sau.gym.admin.agent.config;

import com.sau.gym.admin.agent.store.PendingDraft;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 作者:hfj
 * 功能:Agent Redis配置。
 * 日期: 2026/5/5 16:57
 */
@Configuration
public class AgentRedisConfig {

    /**
     * Agent 草稿专用 RedisTemplate。
     * key：String
     * value：PendingDraft
     */
    @Bean
    public RedisTemplate<String, PendingDraft> agentDraftRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, PendingDraft> template = new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        //key 使用字符串序列化。Redis 中实际 key 类似：agent:draft:user:16
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        //value 使用 JDK 序列化。
        JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();

        return template;
    }
}
