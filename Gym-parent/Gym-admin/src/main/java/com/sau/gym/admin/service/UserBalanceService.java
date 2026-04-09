package com.sau.gym.admin.service;

import com.sau.gym.model.dto.user.UserBalanceDto;

import java.util.Map;

public interface UserBalanceService {

    //前台查询用户余额
    Map<String, Object> GetBalance(Long id);

    //前台用户充值余额
    void Recharge(UserBalanceDto userBalanceDto);
}
