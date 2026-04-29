package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.finance.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentRecordMapper {

    //插入支付流水信息
    void insertOne(PaymentRecord paymentRecord);
}
