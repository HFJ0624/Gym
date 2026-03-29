package com.sau.gym.admin.controller;

import com.sau.gym.admin.utils.EmailEngine;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * 作者:hfj
 * 功能:发送邮箱验证码
 * 日期: 2026/3/29 13:14
 */

@Tag(name = "邮件接口")
@RestController
@RequestMapping("/admin/system/index")
public class EmailController {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final EmailEngine emailEngine = new EmailEngine();

    @Value("${mail.qq.host}")
    private String qqHost;

    @Value("${mail.qq.port}")
    private Integer qqPort;

    @Value("${mail.qq.username}")
    private String qqUsername;

    @Value("${mail.qq.password}")
    private String qqPassword;

    @Value("${mail.163.host}")
    private String mail163Host;

    @Value("${mail.163.port}")
    private Integer mail163Port;

    @Value("${mail.163.username}")
    private String mail163Username;

    @Value("${mail.163.password}")
    private String mail163Password;

    @GetMapping("/sendEmail")
    public Result sendSimpleMail(@RequestParam(value = "emailReceiver") String emailReceiver) {
        try {
            JavaMailSenderImpl mailSender = createMailSender(emailReceiver);
            if (mailSender == null) {
                return Result.build("null", ResultCodeEnum.SYSTEM_ERROR);
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            //发送人名字
            helper.setFrom(mailSender.getUsername());

            //接收人邮件
            helper.setTo(emailReceiver);

            //设置主题
            helper.setSubject("注册验证码");

            String code = generateCode();
            String text = emailEngine.buildContent(code);
            helper.setText(text, true);

            //发送验证码模板
            mailSender.send(message);

            //存入redis
            redisTemplate.opsForValue().set("email:" + emailReceiver, code, 5, TimeUnit.MINUTES);

            return Result.build(code, ResultCodeEnum.SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.build("null", ResultCodeEnum.SYSTEM_ERROR);
        }
    }

    // ====================== 自动创建发送器 ======================
    private JavaMailSenderImpl createMailSender(String email) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.ssl.enable", true);
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.put("mail.smtp.starttls.enable", false);

        if (email.contains("@qq.com")) {
            sender.setHost(qqHost);
            sender.setPort(qqPort);
            sender.setUsername(qqUsername);
            sender.setPassword(qqPassword);

        } else if (email.contains("@163.com")) {
            sender.setHost(mail163Host);
            sender.setPort(mail163Port);
            sender.setUsername(mail163Username);
            sender.setPassword(mail163Password);

        } else {
            return null;
        }

        sender.setJavaMailProperties(prop);
        sender.setDefaultEncoding("UTF-8");
        return sender;
    }

    private String generateCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }
}
