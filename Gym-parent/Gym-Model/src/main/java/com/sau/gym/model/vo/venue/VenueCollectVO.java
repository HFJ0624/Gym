package com.sau.gym.model.vo.venue;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/31 9:35
 */
@Data
@Schema(description = "场馆收藏返回类")
public class VenueCollectVO {

    @Schema(description = "收藏id")
    private Long id;

    @Schema(description = "收藏人名称")
    private String username;

    @Schema(description = "收藏人头像")
    private String avatar;

    @Schema(description = "对应场馆名称")
    private String venueName;

    @Schema(description = "是否收藏")
    private Integer collect;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
