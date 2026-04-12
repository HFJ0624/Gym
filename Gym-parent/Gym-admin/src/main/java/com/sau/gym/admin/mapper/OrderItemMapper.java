package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.order.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemMapper {

    //插入订单明细
    void insertOrderItem(OrderItem orderItem);
}
