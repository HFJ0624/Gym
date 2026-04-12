package com.sau.gym.model.vo.order;

import com.sau.gym.model.entity.order.OrderItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/12 21:55
 */
@Data
@Schema(description = "订单返回类")
public class OrderDetailVO {

    private Long id;                    // 订单ID
    private String orderNo;             // 订单号

    private Date payTime;          // 创建时间

    private Integer status;             // 状态 0取消 1待支付 2已支付 3完成
    private BigDecimal totalAmount;     // 总金额
    private Integer totalQuantity;      // 商品总数量
    private String remark;              // 备注
    private List<OrderItem> items;    // 商品列表
}
