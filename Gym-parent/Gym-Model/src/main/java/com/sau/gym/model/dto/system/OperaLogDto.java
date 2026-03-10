package com.sau.gym.model.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/10 16:07
 */
@Data
@Schema(description = "请求参数实体类")
public class OperaLogDto {

    @Schema(description = "模块名称")
    private String title;

    @Schema(description = "操作用户名")
    private String operaName;

    @Schema(description = "开始时间")
    private String createTimeBegin;

    @Schema(description = "结束时间")
    private String createTimeEnd;
}
