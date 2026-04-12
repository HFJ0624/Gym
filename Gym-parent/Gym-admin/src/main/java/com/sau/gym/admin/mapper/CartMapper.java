package com.sau.gym.admin.mapper;

import com.sau.gym.model.entity.shopping.Cart;
import com.sau.gym.model.vo.cart.CartItemVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CartMapper {

    //获取购物车详情
    List<CartItemVO> GetCartList(Long id);

    //查询购物车是否已存在该商品
    Cart selectCartExist(Long userId, Long goodsId);

    //存在则数量累加
    void addQuantity(Long id, Integer quantity);

    //购物车不存在则新增
    void insert(Cart cart);

    //删除购物车商品
    void DeleteCartItem(Long id);

    //更新购物车数量
    void UpdateCartItem(Long cartId, Integer quantity);
}
