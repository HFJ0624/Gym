package com.sau.gym.model.vo.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/16 14:39
 */
@Data
@Schema(description = "订单实体类")
public class OrderVO {

    @Schema(description = "订单用户名")
    private String username;

    @Schema(description = "订单用户电话")
    private String phone;

    @Schema(description = "订单编号")
    private String orderNo;

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
