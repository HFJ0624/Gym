package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.finance.RefundRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RefundRecordMapper {

    //插入退款流水信息
    void insertOne(RefundRecord refundRecord);
}
