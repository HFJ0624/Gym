package com.sau.gym.model.dto.venue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/31 9:42
 */
@Data
@Schema(description = "请求参数实体类")
public class VenueCollectDto {

    private String username;

    private String venueName;
}
