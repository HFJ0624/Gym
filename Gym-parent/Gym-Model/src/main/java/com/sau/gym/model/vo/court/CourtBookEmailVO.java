package com.sau.gym.model.vo.court;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/27 21:25
 */
@Data
@Schema(description = "场地预约接收实体类")
public class CourtBookEmailVO {

    private Long id;

    private String orderNo;

    private Long userId;

    private Long courtId;

    private String bookingDate;

    private String startTime;

    private String endTime;

    private Integer status;

    private String remark;

    private Integer isReminded;

    private String createTime;

    private Double totalPrice;

    // 关联用户表字段（用户姓名、邮箱）
    private String username;

    private String email;

    // 关联场地表字段（场地名称、类型）
    private String courtName;

    private String courtType;
}
