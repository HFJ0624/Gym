package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.order.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {

    //插入到数据库
    void insertOrder(Order order);
}
