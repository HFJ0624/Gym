package com.sau.gym.admin.service.impl;

import com.sau.gym.admin.mapper.UserBalanceMapper;
import com.sau.gym.admin.service.UserBalanceService;
import com.sau.gym.model.dto.user.UserBalanceDto;
import com.sau.gym.model.entity.user.UserBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/9 20:27
 */
@Service
public class UserBalanceServiceImpl implements UserBalanceService {

    @Autowired
    private UserBalanceMapper userBalanceMapper;

    //前台查询用户余额
    @Override
    public Map<String, Object> GetBalance(Long id) {
        //1.先从数据库查,如果数据库没有数据,插入一条余额为0的数据
        UserBalance userBalance = userBalanceMapper.GetBalanceById(id);

        //2.没有插入一条余额为0的数据
        if (userBalance  == null){
            userBalanceMapper.insertOne(id);
            userBalance = userBalanceMapper.GetBalanceById(id);
        }

        //3.返回
        HashMap<String, Object> map = new HashMap<>();
        map.put("balance",userBalance);
        return map;
    }

    //前台用户充值余额
    @Override
    public void Recharge(UserBalanceDto userBalanceDto) {
        userBalanceMapper.Recharge(userBalanceDto);
    }
}
