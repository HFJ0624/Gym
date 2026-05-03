package com.sau.gym.model.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/3 21:08
 */
@Data
@Schema(description = "统计金额实体类")
public class TurnoverVo {

    private String day;

    private BigDecimal turnover;
}
