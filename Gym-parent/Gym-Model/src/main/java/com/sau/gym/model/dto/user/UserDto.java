package com.sau.gym.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/5 16:46
 */
@Data
@Schema(description = "请求参数实体类")
public class UserDto {

    @Schema(description = "搜索关键字")
    private String keyword;

    @Schema(description = "开始时间")
    private String createTimeBegin;

    @Schema(description = "结束时间")
    private String createTimeEnd;
}
