package com.sau.gym.admin.mapper;

import com.sau.gym.model.dto.order.OrdersDto;
import com.sau.gym.model.entity.order.Order;
import com.sau.gym.model.vo.order.OrderVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderMapper {

    //插入到数据库
    void insertOrder(Order order);

    //获取购物订单列表
    List<Order> getOrderList(Long id,Integer status);

    //订单查询列表
    List<OrderVO> findByPage(OrdersDto ordersDto);
}
