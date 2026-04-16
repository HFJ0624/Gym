package com.sau.gym.model.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/16 14:35
 */
@Data
@Schema(description = "请求参数实体类")
public class OrdersDto {

    private String username;

    private String remark;
}
