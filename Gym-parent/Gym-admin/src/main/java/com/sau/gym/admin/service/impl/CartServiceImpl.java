package com.sau.gym.admin.service.impl;

import com.sau.gym.admin.mapper.BeverageMapper;
import com.sau.gym.admin.mapper.CartMapper;
import com.sau.gym.admin.service.CartService;
import com.sau.gym.model.dto.shopping.CartDto;
import com.sau.gym.model.dto.shopping.CartItemDto;
import com.sau.gym.model.entity.shopping.Beverage;
import com.sau.gym.model.entity.shopping.Cart;
import com.sau.gym.model.vo.cart.CartItemVO;
import com.sau.gym.model.vo.cart.CartListVO;
import com.sau.gym.utils.AuthContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/12 9:53
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private BeverageMapper beverageMapper;

    //获取购物车详情
    @Override
    public CartListVO GetCartList(Long id) {
        //1. 调用Mapper查询购物车数据，返回List<CartItemVO>
        List<CartItemVO> cartItemList = cartMapper.GetCartList(id);

        //2. 封装到你已有的CartListVO中
        CartListVO cartListVO = new CartListVO();
        cartListVO.setList(cartItemList);
        return cartListVO;
    }

    //添加商品到购物车
    @Override
    public void AddToCart(CartDto cartDto) {
        Long userId = AuthContextUtil.get().getId();

        // 1. 查询购物车是否已存在该商品
        Cart cartDb = cartMapper.selectCartExist(userId, cartDto.getGoodsId());

        if (cartDb != null) {
            //2.存在则数量累加
            cartMapper.addQuantity(cartDb.getId(), cartDto.getQuantity());
        } else {
            //3.不存在则新增
            Cart cart = new Cart();
            Beverage beverage = beverageMapper.selectById(cartDto.getGoodsId());

            cart.setUserId(userId);
            cart.setGoodsId(cartDto.getGoodsId());
            cart.setGoodsName(beverage.getGoodsName()); // 从商品表查
            cart.setImage(beverage.getImage());
            cart.setPrice(beverage.getPrice());
            cart.setQuantity(cartDto.getQuantity());
            cartMapper.insert(cart);
        }
    }

    //更新购物车数量
    @Override
    public void UpdateCartItem(CartItemDto cartItemDto) {
        //更新购物车数量
        cartMapper.UpdateCartItem(cartItemDto.getCartId(),cartItemDto.getQuantity());
    }

    //删除购物车商品
    @Override
    public void DeleteCartItem(Long id) {
        cartMapper.DeleteCartItem(id);
    }
}
