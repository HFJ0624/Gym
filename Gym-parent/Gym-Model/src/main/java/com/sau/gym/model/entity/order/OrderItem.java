package com.sau.gym.model.entity.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/12 15:43
 */
@Data
@Schema(description = "订单详情实体类")
public class OrderItem {

    @Schema(description = "订单明细id")
    private Long id;

    @Schema(description = "订单id")
    private Long orderId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "商品id")
    private Long goodsId;

    @Schema(description = "商品名称")
    private String goodsName;

    @Schema(description = "商品图片")
    private String image;

    @Schema(description = "商品单价")
    private BigDecimal price;

    @Schema(description = "商品数量")
    private Integer quantity;
}
