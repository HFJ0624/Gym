package com.sau.gym.model.entity.notice;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sau.gym.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/29 15:58
 */
@Data
@Schema(description = "系统通知实体类")
public class Notification extends BaseEntity {

    @Schema(description = "通知用户id")
    private Long userId;

    @Schema(description = "通知标题")
    private String title;

    @Schema(description = "通知内容")
    private String content;

    @Schema(description = "通知类型：1预约，2支付，3退款，4公告，5系统，6Agent")
    private Integer type;

    @Schema(description = "业务id")
    private Long businessId;

    @Schema(description = "业务编号，如订单号")
    private String businessNo;

    @Schema(description = "业务类型：1预约订单，2商城订单，3公告")
    private Integer businessType;

    @Schema(description = "阅读状态：0未读，1已读")
    private Integer readStatus;

    @Schema(description = "阅读时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;
}
