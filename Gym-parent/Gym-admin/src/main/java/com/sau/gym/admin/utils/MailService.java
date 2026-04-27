package com.sau.gym.admin.utils;

import jakarta.annotation.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/27 21:09
 */
@Service
public class MailService {

    // 注入唯一的邮件发送器
    @Resource
    private JavaMailSender mailSender;

    // 发件人（和配置一致）
    private final String SENDER = "342586916@qq.com";

    // 发送邮件（唯一方法）
    public void sendMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(SENDER);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }
}
