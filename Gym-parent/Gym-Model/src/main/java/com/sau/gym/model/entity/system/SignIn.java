package com.sau.gym.model.entity.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sau.gym.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.Date;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/27 10:47
 */
@Data
@Schema(description = "签到实体类")
public class SignIn extends BaseEntity {

    @Schema(description = "唯一签到标识（UUID）")
    private String token;

    @Schema(description = "签到名称")
    private String name;

    @Schema(description = "签到人电话")
    private String phone;

    @Schema(description = "0-未签到 1-已到场")
    private Integer status;

    @Schema(description = "签到时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date signTime;
}
