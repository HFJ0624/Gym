package com.sau.gym.model.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/16 9:49
 */
@Data
@Schema(description = "用户性别实体类")
public class UserVo {

    @Schema(description = "用户性别")
    private String sex;

    @Schema(description = "性别数量")
    private Integer number;
}
