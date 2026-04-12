package com.sau.gym.admin.service;

import com.github.pagehelper.PageInfo;
import com.sau.gym.model.dto.order.OrderDto;
import com.sau.gym.model.entity.order.Order;
import com.sau.gym.model.vo.order.OrderDetailVO;

public interface OrderService {

    //创建订单
    void CreateShoppingOrder(OrderDto orderDto);

    //获取购物订单列表
    PageInfo<OrderDetailVO> getOrderList(Integer page, Integer limit, Integer status);
}
