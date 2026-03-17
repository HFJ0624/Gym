package com.sau.gym.model.vo.venue;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/17 9:21
 */
@Data
@Schema(description = "场馆预约接收实体类")
public class VenueBookVO {

    private Long id;

    private String orderNo;

    private String userName;

    private String realName;

    private String venueName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date bookingDate;

    private String remark;

    private String location;

    private Integer personNum;

    private String venueType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
