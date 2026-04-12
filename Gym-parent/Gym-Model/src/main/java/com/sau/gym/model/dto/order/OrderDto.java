package com.sau.gym.model.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/12 15:09
 */
@Data
@Schema(description = "请求参数实体类")
public class OrderDto {

    private List<Long> cartIds;

    private String remark;
}
