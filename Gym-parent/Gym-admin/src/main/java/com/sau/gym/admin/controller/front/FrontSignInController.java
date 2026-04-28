package com.sau.gym.admin.controller.front;

import com.google.zxing.WriterException;
import com.sau.gym.admin.service.SignInService;
import com.sau.gym.admin.utils.QRCodeUtil;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.system.SignIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 作者:hfj
 * 功能:前端生成签到二维码功能
 * 日期: 2026/4/27 10:56
 */
@RestController
@RequestMapping("/front/sign-in")
public class FrontSignInController {

    @Autowired
    private SignInService signInService;

    //生成签到二维码
    @PostMapping("/generate-qrcode")
    public Result<Map<String, String>> generateQRCode(@RequestParam String name, @RequestParam(required = false) String phone) throws WriterException, IOException {
        SignIn signIn = signInService.generateSignInQRCode(name, phone);
        // 二维码内容：前端签到页面地址+token（生产环境替换为真实域名）
        String qrContent = "http://172.26.254.234:9601/front/sign-in/page?token=" + signIn.getToken();

        String qrBase64 = QRCodeUtil.generateQRCodeBase64(qrContent);

        Map<String, String> result = new HashMap<>();
        result.put("token", signIn.getToken());
        result.put("qrBase64", qrBase64);
        result.put("qrContent", qrContent);
        return Result.build(result, ResultCodeEnum.SUCCESS);
    }

    //签到接口（手机扫码后前端调用）
    @GetMapping("/do-sign")
    public Result doSign(@RequestParam String token) {
        String s = signInService.signIn(token);
        return Result.build(s,ResultCodeEnum.SUCCESS);
    }

    //签到结果页面（手机扫码跳转的页面）
    @GetMapping(value = "/page", produces = "text/html;charset=UTF-8")
    public String signInPage(@RequestParam String token) {
        // 1. 先执行签到逻辑，更新数据库
        String result = signInService.signIn(token);

        // 2. 生成HTML页面，显示结果+自动跳转
//        return "<!DOCTYPE html>" +
//                "<html lang='zh-CN'>" +
//                "<head>" +
//                "<meta charset='UTF-8'>" +
//                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
//                "<title>签到结果</title>" +
//                "<style>" +
//                "body{display:flex;justify-content:center;align-items:center;height:100vh;margin:0;font-family:Arial;background:#f5f5f5;}" +
//                ".result{text-align:center;padding:30px;background:white;border-radius:10px;box-shadow:0 0 10px rgba(0,0,0,0.1);}" +
//                ".status{font-size:24px;font-weight:bold;margin:20px 0;}" +
//                ".success{color:#42b983;}" +
//                ".fail{color:#f56c6c;}" +
//                ".tip{color:#999;margin-top:10px;}" +
//                ".jump-btn{margin-top:20px;padding:10px 20px;background:#42b983;color:white;border:none;border-radius:5px;cursor:pointer;}" +
//                "</style>" +
//                "</head>" +
//                "<body>" +
//                "<div class='result'>" +
//                "<div class='status " + (result.contains("已到场") ? "success" : "fail") + "'>" + result + "</div>" +
//                "<div class='tip'>" + (result.contains("已到场") ? "3秒后自动跳转首页..." : "请联系管理员") + "</div>" +
//                "<div style='margin-top:20px;color:#999;font-size:12px;'>签到码：" + token + "</div>" +
//                (result.contains("已到场") ? "<button class='jump-btn' onclick='jump()'>立即跳转首页</button>" : "") +
//                "</div>" +
//                "<script>" +
//                "function jump() {" +
//                "  window.location.href = 'http://172.20.10.12:3002/front/index';" +
//                "}" +
//                // 签到成功才执行自动跳转
//                (result.contains("已到场") ? "setTimeout(jump, 3000);" : "") +
//                "</script>" +
//                "</body>" +
//                "</html>";

        // 2. 纯后端HTML页面，直接显示结果，不用跳转
        return "<!DOCTYPE html>" +
                "<html lang='zh-CN'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<title>签到成功</title>" +
                "<style>" +
                "*{margin:0;padding:0;box-sizing:border-box;}" +
                "body{min-height:100vh;background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);font-family:Arial, sans-serif;display:flex;justify-content:center;align-items:center;padding:20px;}" +
                ".card{background:white;border-radius:20px;padding:40px 30px;text-align:center;box-shadow:0 20px 60px rgba(0,0,0,0.3);max-width:350px;width:100%;}" +
                ".icon{font-size:80px;margin-bottom:20px;}" +
                "h1{font-size:28px;color:#333;margin-bottom:10px;}" +
                "p{color:#666;font-size:16px;margin-bottom:20px;}" +
                ".token{font-size:12px;color:#999;word-break:break-all;margin-top:30px;}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='card'>" +
                "<div class='icon'>" + (result.contains("已到场") ? "✅" : "❌") + "</div>" +
                "<h1>" + result + "</h1>" +
                "<p>" + (result.contains("已到场") ? "签到已完成，欢迎您！" : "请联系管理员处理") + "</p>" +
                "<div class='token'>签到码：" + token + "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    //查询签到记录（网页端管理员查看）
    @GetMapping("/query")
    public Result querySignIn(@RequestParam String token) {
        SignIn signIn = signInService.getSignInByToken(token);
        return Result.build(signIn,ResultCodeEnum.SUCCESS);
    }

}
