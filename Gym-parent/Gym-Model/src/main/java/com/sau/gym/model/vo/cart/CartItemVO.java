package com.sau.gym.model.vo.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/12 9:22
 */
@Data
@Schema(description = "购物车物品实体类")
public class CartItemVO {

    private Long id;            // 购物车ID

    private Long goodsId;       // 商品ID

    private String goodsName;   // 商品名

    private String image;       // 图片

    private BigDecimal price;   // 单价

    private Integer quantity;   // 数量
}
