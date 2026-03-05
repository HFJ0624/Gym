package com.sau.gym.model.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/5 10:23
 */
@Data
@Schema(description = "请求参数实体类")
public class RoleDto {

    @Schema(description = "角色名称")
    private String roleName;
}
