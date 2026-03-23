package com.sau.gym.model.entity.venue;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sau.gym.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 作者:hfj
 * 功能:场馆实体类
 * 日期: 2026/3/3 21:12
 */
@Data
@Schema(description = "场馆实体类")
public class Venue extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "场馆名称")
    private String venueName;

    @Schema(description = "场馆类型")
    private String venueType;

    @Schema(description = "场馆位置")
    private String location;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "场馆最大容量")
    private Integer capacity;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "开始营业时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date openTime;

    @Schema(description = "结束营业时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date closeTime;

    @Schema(description = "场馆图片")
    private String avatar;

    @Schema(description = "状态：1为正常，0为禁止")
    private Integer status;
}
