package com.sau.gym.model.entity.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 作者:hfj
 * 功能:基础实体类
 * 日期: 2026/3/3 20:28
 */
@Data
public class BaseEntity implements Serializable {

    @Schema(description = "唯一标识")
    private Long id;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date creatTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    @Schema(description = "是否删除")
    private Integer isDeleted;
}
