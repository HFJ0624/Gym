package com.sau.gym.model.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/6 16:54
 */
@Data
@Schema(description = "请求参数实体类")
public class AssignRoleDto {

    // 用户的id
    @Schema(description = "用户id")
    private Long userId;

    // 角色id
    @Schema(description = "角色的各个id")
    private List<Long> roleIdList;
}
