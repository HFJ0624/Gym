package com.sau.gym.model.entity.venue;

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
    private String name;

    @Schema(description = "场馆地址")
    private String address;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "设施(如:淋浴、更衣室)")
    private String facilities;

    @Schema(description = "开始营业时间")
    private Date openTime;

    @Schema(description = "结束营业时间")
    private Date closeTime;

    @Schema(description = "状态：1为正常，0为禁止")
    private Integer status;
}
