package com.sau.gym.model.vo.court;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 作者:hfj
 * 功能:场地专门接收联查结果
 * 日期: 2026/3/10 10:39
 */
@Data
@Schema(description = "场地接收实体类")
public class CourtVO {

    // court表字段
    private Long id;

    private Long venueId;

    private String name;

    private String type;

    private Integer capacity;

    private BigDecimal price;

    private String avatar;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    // venue表关联字段
    private String venueName;

    private String location;

    private String phone;
}
