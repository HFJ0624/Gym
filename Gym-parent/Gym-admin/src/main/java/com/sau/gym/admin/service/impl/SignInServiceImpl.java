package com.sau.gym.admin.service.impl;

import com.sau.gym.admin.mapper.SignInMapper;
import com.sau.gym.admin.service.SignInService;
import com.sau.gym.model.entity.system.SignIn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/27 10:55
 */
@Service
public class SignInServiceImpl implements SignInService {

    @Autowired
    private SignInMapper signInMapper;

    // 生成签到二维码（返回token+二维码Base64）
    @Override
    public SignIn generateSignInQRCode(String name, String phone) {
        String token = UUID.randomUUID().toString().replace("-", "");
        SignIn signIn = new SignIn();
        signIn.setToken(token);
        signIn.setName(name);
        signIn.setPhone(phone);
        signInMapper.insertSignIn(signIn);
        return signIn;
    }

    // 执行签到（更新状态）
    @Transactional
    @Override
    public String signIn(String token) {
        SignIn signIn = signInMapper.selectByToken(token);
        if (signIn == null) {
            return "无效的签到码";
        }
        if (signIn.getStatus() == 1) {
            return "已到场，无需重复签到";
        }
        int rows = signInMapper.updateStatusByToken(token);
        return rows > 0 ? "已到场" : "签到失败";
    }

    // 查询签到记录（网页端用）
    @Override
    public SignIn getSignInByToken(String token) {
        return signInMapper.selectByToken(token);
    }
}
