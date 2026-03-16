package com.sau.gym.model.dto.venue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/16 18:18
 */
@Data
@Schema(description = "请求参数实体类")
public class CourtBookDto {

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "场地名称")
    private String courtName;
}
