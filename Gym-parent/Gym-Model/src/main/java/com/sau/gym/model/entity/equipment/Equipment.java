package com.sau.gym.model.entity.equipment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sau.gym.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/11 16:35
 */
@Data
@Schema(description = "器械实体类")
public class Equipment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "健身房名称")
    private String gymName;

    @Schema(description = "器械名称")
    private String name;

    @Schema(description = "器械品牌")
    private String brand;

    @Schema(description = "器械价格")
    private BigDecimal price;

    @Schema(description = "器械数量")
    private Integer quantity;

    @Schema(description = "状态：0报废 1可用 2维护中")
    private Integer status;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "图片路径")
    private String imageUrl;

    @Schema(description = "购买日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date purchaseDate;

}
