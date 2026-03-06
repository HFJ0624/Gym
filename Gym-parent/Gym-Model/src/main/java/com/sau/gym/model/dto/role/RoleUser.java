package com.sau.gym.model.dto.role;

import com.sau.gym.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/6 16:55
 */
@Data
@Schema(description = "请求参数实体类")
public class RoleUser extends BaseEntity {

    // 角色id
    @Schema(description = "角色id")
    private Long roleId;

    // 用户的id
    @Schema(description = "用户id")
    private Long userId;
}
