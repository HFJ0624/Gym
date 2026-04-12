package com.sau.gym.model.entity.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sau.gym.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/12 15:39
 */
@Data
@Schema(description = "订单实体类")
public class Order extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "订单总价格")
    private BigDecimal totalPrice;

    @Schema(description = "0待支付 1已支付 2已发货 3已完成 -1已取消")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;
}
