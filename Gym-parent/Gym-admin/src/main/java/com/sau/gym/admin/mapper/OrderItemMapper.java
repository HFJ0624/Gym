package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.order.OrderItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderItemMapper {

    //插入订单明细
    void insertOrderItem(OrderItem orderItem);

    //根据id获取商品信息
    List<OrderItem> getOrderById(Long id);
}
