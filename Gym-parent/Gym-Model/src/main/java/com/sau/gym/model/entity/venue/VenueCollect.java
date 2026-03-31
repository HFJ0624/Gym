package com.sau.gym.model.entity.venue;

import com.sau.gym.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/31 9:20
 */
@Data
@Schema(description = "场馆收藏实体类")
public class VenueCollect extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "场馆id")
    private Long venueId;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "是否收藏，0：没收藏，1：收藏")
    private Integer collect;
}
