package com.sau.gym.model.vo.venue;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/1 8:43
 */
@Data
@Schema(description = "场馆访问返回类")
public class VenueVisitVO {

    @Schema(description = "访问id")
    private Long id;

    @Schema(description = "对应场馆名称")
    private String venueName;

    @Schema(description = "场馆访问量")
    private Long nums;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
