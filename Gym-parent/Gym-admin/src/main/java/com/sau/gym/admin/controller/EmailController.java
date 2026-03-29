package com.sau.gym.admin.controller;

import com.sau.gym.admin.utils.EmailEngine;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * 作者:hfj
 * 功能:发送邮箱验证码
 * 日期: 2026/3/29 13:14
 */

@Tag(name = "邮件接口")
@RestController
@RequestMapping(value = "/admin/system/index")
public class EmailController {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //读取yml文件中username的值并赋值给form
    @Value("${spring.mail.username}")
    private String from;

    @GetMapping("/sendEmail")
    public Result sendSimpleMail(@RequestParam(value = "emailReceiver") String emailReceiver) throws MessagingException {
        EmailEngine emailEngine = new EmailEngine();

        // 构建一个邮件对象
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // 设置邮件发送者
        helper.setFrom(from);
        // 设置邮件接收者
        helper.setTo(emailReceiver);
        // 设置邮件的主题
        helper.setSubject("注册验证码");
        // 设置邮件的正文
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int r = random.nextInt(10);
            code.append(r);
        }
        String text = emailEngine.buildContent(code.toString());
        helper.setText(text,true);

        // 发送邮件
        try {
            javaMailSender.send(message);

            String codeString = code.toString();
            //存入redis里面
            redisTemplate.opsForValue().set("email:" + emailReceiver,codeString,5, TimeUnit.MINUTES);

            return Result.build(code.toString(), ResultCodeEnum.SUCCESS);
        } catch (MailException e) {
            e.printStackTrace();
        }
        return Result.build("null", ResultCodeEnum.SYSTEM_ERROR);
    }
}
