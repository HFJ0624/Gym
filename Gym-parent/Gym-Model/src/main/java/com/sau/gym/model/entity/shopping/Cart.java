package com.sau.gym.model.entity.shopping;

import com.sau.gym.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/12 10:01
 */
@Data
@Schema(description = "购物车实体类")
public class Cart extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "商品id")
    private Long goodsId;

    @Schema(description = "商品名称")
    private String goodsName;

    @Schema(description = "商品图片地址")
    private String image;

    @Schema(description = "商品价格")
    private BigDecimal price;

    @Schema(description = "商品数量")
    private Integer quantity;
}
