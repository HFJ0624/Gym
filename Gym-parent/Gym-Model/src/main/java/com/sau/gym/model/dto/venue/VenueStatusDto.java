package com.sau.gym.model.dto.venue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/25 15:07
 */
@Data
@Schema(description = "请求参数实体类")
public class VenueStatusDto {

    @Schema(description = "场馆id")
    private Long id;

    @Schema(description = "场馆状态")
    private String status;
}
