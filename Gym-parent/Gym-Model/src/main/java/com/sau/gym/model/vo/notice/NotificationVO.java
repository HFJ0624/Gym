package com.sau.gym.model.vo.notice;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/29 16:03
 */
@Data
@Schema(description = "系统通知返回类")
public class NotificationVO {

    private Long id;

    private String title;

    private String content;

    private Integer type;

    private String typeName;

    private Long businessId;

    private String businessNo;

    private Integer businessType;

    private Integer readStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;
}
