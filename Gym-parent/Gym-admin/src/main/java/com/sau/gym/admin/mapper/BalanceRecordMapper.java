package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.finance.BalanceRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BalanceRecordMapper {

    //插入流水信息
    void insertOne(BalanceRecord balanceRecord);
}
