package com.sau.gym.model.entity.system;

import com.sau.gym.model.entity.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/3/10 16:06
 */
@Data
@Schema(description = "OperaLog")
public class OperaLog extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "模块标题")
    private String title;

    @Schema(description = "方法名称")
    private String method;

    @Schema(description = "请求方式")
    private String requestMethod;

    private Integer businessType ;			// 业务类型（0其它 1新增 2修改 3删除）

    @Schema(description = "操作类别（0其它 1后台用户 2手机端用户）")
    private String operatorType;

    @Schema(description = "操作人员")
    private String operaName;

    @Schema(description = "请求URL")
    private String operaUrl;

    @Schema(description = "主机地址")
    private String operaIp;

    @Schema(description = "请求参数")
    private String operaParam;

    @Schema(description = "返回参数")
    private String jsonResult;

    @Schema(description = "操作状态（0正常 1异常）")
    private Integer status;

    @Schema(description = "错误消息")
    private String errorMsg;
}
