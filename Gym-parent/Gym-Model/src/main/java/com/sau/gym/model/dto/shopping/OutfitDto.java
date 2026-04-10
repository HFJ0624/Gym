package com.sau.gym.model.dto.shopping;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/10 18:10
 */
@Data
@Schema(description = "请求参数实体类")
public class OutfitDto {

    private String name;

    private Integer category;
}
