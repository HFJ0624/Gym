package com.sau.gym.model.entity.user;

import com.sau.gym.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:用户实体类
 * 日期: 2026/3/3 20:44
 */
@Data
@Schema(description = "用户实体类")
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "性别")
    private String sex;

    @Schema(description = "电话号码")
    private String phone;

    @Schema(description = "邮件")
    private String email;

    @Schema(description = "角色")
    private String role;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "状态：1为正常，0为禁止")
    private Integer status;
}
