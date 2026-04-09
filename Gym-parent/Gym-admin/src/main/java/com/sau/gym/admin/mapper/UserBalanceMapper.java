package com.sau.gym.admin.mapper;

import com.sau.gym.model.dto.user.UserBalanceDto;
import com.sau.gym.model.entity.user.UserBalance;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserBalanceMapper {

    //1.先从数据库查,如果数据库没有数据,插入一条余额为0的数据
    UserBalance GetBalanceById(Long id);

    //插入一条数据
    void insertOne(Long id);

    //前台用户充值余额
    void Recharge(UserBalanceDto userBalanceDto);
}
