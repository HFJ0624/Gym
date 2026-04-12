package com.sau.gym.model.vo.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/12 9:23
 */
@Data
@Schema(description = "购物车列表实体类")
public class CartListVO {

    private List<CartItemVO> list;
}
