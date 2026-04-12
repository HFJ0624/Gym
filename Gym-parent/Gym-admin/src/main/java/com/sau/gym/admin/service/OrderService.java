package com.sau.gym.admin.service;

import com.sau.gym.model.dto.order.OrderDto;

public interface OrderService {

    //创建订单
    void CreateShoppingOrder(OrderDto orderDto);
}
