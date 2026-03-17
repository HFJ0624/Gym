package com.sau.gym.model.dto.venue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/17 9:18
 */
@Data
@Schema(description = "请求参数实体类")
public class VenueBookDto {

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "场馆名称")
    private String venueName;

    @Schema(description = "场馆地址")
    private String location;
}
