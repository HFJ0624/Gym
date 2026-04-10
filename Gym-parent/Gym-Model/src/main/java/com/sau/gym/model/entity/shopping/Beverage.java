package com.sau.gym.model.entity.shopping;

import com.sau.gym.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/9 21:48
 */
@Data
@Schema(description = "饮品零食实体类")
public class Beverage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "商品名称")
    private String goodsName;

    @Schema(description = "分类：1=饮品 2=零食 3=日用品")
    private Integer category;

    @Schema(description = "售价")
    private BigDecimal price;

    @Schema(description = "成本价")
    private BigDecimal cost;

    @Schema(description = "库存数量")
    private Integer stock;

    @Schema(description = "商品图片地址")
    private String image;

    @Schema(description = "状态：1=上架 2=下架")
    private Integer status;

    @Schema(description = "备注（冰/常温/热销）")
    private String remark;
}
