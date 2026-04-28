package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.system.SignInDto;
import com.sau.gym.model.entity.system.SignIn;

public interface SignInService {
    
    
    SignIn generateSignInQRCode(String name, String phone);

    String signIn(String token);

    SignIn getSignInByToken(String token);

    //分页查看所有签到记录
    PageInfo<SignIn> findByPage(Integer current, Integer limit, SignInDto signInDto);
}
