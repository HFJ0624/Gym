package com.sau.gym.model.dto.equipment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/11 19:25
 */
@Data
@Schema(description = "请求参数实体类")
public class EquipmentDto {

    @Schema(description = "健身房名称")
    private String gymName;

    @Schema(description = "器械名称")
    private String name;

    @Schema(description = "品牌")
    private String brand;
}
