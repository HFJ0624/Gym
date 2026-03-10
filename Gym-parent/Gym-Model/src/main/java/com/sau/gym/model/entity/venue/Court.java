package com.sau.gym.model.entity.venue;

import com.sau.gym.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 作者:hfj
 * 功能:场地实体类
 * 日期: 2026/3/3 21:44
 */
@Data
@Schema(description = "场地实体类")
public class Court extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "所属场馆ID")
    private Long venueId;

    @Schema(description = "场地名称")
    private String name;

    @Schema(description = "场地类型")
    private String type;

    @Schema(description = "容纳人数")
    private Integer capacity;

    @Schema(description = "每小时价格")
    private BigDecimal price;

    @Schema(description = "状态：1为正常,0为维护/不可用")
    private Integer status;
}
