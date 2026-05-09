package com.sau.gym.model.dto.refund;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/9 11:24
 */
@Data
@Schema(description = "退款审核")
public class RefundAuditDto {

    @Schema(description = "退款申请ID")
    private Long id;

    @Schema(description = "审核备注")
    private String auditRemark;
}
