package com.sau.gym.model.vo.rag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Date;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/1 10:24
 */
@Data
@Schema(description = "RAG场地同步VO")
public class RagCourtSyncVO {

    @Schema(description = "场地ID")
    private Long courtId;

    @Schema(description = "场地名称")
    private String courtName;

    @Schema(description = "场地类型，例如：篮球场、足球场、羽毛球场")
    private String type;

    @Schema(description = "所属场馆ID")
    private Long venueId;

    @Schema(description = "所属场馆名称")
    private String venueName;

    @Schema(description = "场地价格")
    private BigDecimal price;

    @Schema(description = "场地容量")
    private Integer capacity;

    @Schema(description = "场地说明")
    private String description;

    @Schema(description = "开放开始时间")
    private LocalTime openTime;

    @Schema(description = "开放结束时间")
    private LocalTime closeTime;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "是否删除")
    private Integer isDeleted;

    @Schema(description = "更新时间")
    private Date updateTime;
}
