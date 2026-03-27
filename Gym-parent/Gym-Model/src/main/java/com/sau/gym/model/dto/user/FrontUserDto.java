package com.sau.gym.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/27 16:55
 */
@Data
@Schema(description = "修改用户基本信息实体类")
public class FrontUserDto {

    private Long id;

    private String realName;

    private String phone;

    private String sex;

    private String email;

    private String oldPassword;

    private String newPassword;

    private String avatar;
}
