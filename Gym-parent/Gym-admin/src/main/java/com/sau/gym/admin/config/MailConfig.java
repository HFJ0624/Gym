package com.sau.gym.admin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * 作者:hfj
 * 功能:邮箱配置类
 * 日期: 2026/4/27 21:12
 */
@Configuration
public class MailConfig {

    @Autowired
    private Environment env;

    // 仅保留 QQ 邮箱发送器
    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();

        // 读取你的配置（前缀 mail.qq. 严格匹配）
        String prefix = "mail.qq.";

        // 基础配置
        sender.setHost(env.getProperty(prefix + "host"));
        sender.setPort(465);
        sender.setUsername(env.getProperty(prefix + "username"));
        sender.setPassword(env.getProperty(prefix + "password"));
        sender.setDefaultEncoding("UTF-8");

        // 核心：只放有效值，杜绝 null 空指针
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        sender.setJavaMailProperties(props);
        return sender;
    }
}
