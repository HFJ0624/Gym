package com.sau.gym.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.sau.gym.admin.mapper.SysUserMapper;
import com.sau.gym.admin.service.SysUserService;
import com.sau.gym.common.exception.SauException;
import com.sau.gym.model.dto.system.LoginDto;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.user.User;
import com.sau.gym.model.vo.system.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/4 15:25
 */
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private RedisTemplate<String , String> redisTemplate;

    //登录接口
    @Override
    public LoginVo login(LoginDto loginDto) {
        //1.根据用户名查询用户
        User user = sysUserMapper.selectByUserName(loginDto.getUserName());

        if (user == null){
            throw new SauException(ResultCodeEnum.LOGIN_ERROR);
        }

        //2.验证密码是否正确
        String inputPassword = loginDto.getPassword();
        String md5InputPassword = DigestUtils.md5DigestAsHex(inputPassword.getBytes());
        if(!md5InputPassword.equals(user.getPassword())) {
            throw new RuntimeException("用户名或者密码错误") ;
        }

        //3.生成令牌，保存数据到Redis中
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set("user:login:" + token , JSON.toJSONString(user) , 30 , TimeUnit.DAYS);

        //4.构建响应结果对象
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        loginVo.setRefresh_token("");

        //5.返回
        return loginVo;
    }
}
