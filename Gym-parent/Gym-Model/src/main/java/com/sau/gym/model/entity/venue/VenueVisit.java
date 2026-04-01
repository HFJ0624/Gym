package com.sau.gym.model.entity.venue;

import com.sau.gym.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/1 8:33
 */
@Data
@Schema(description = "场馆访问量实体类")
public class VenueVisit extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "场馆Id")
    private Long venueId;

    @Schema(description = "场馆位置")
    private Long nums;
}
