package com.sau.gym.model.dto.venue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/10 10:05
 */
@Data
@Schema(description = "请求参数实体类")
public class CourtDto {

    @Schema(description = "场地名称")
    private String name;

    @Schema(description = "场地类型")
    private String type;
}
