package com.sau.gym.model.vo.court;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/16 18:20
 */
@Data
@Schema(description = "场地预约接收实体类")
public class CourtBookVO {

    private Long id;

    private String orderNo;

    private String userName;

    private String courtName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date bookingDate;

    private String remark;

    private BigDecimal totalPrice;

    private String type;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
