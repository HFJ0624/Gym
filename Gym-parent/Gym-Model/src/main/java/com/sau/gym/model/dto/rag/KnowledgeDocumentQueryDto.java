package com.sau.gym.model.dto.rag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/4/30 22:09
 */
@Data
@Schema(description = "RAG知识文档查询参数")
public class KnowledgeDocumentQueryDto {

    @Schema(description = "页码")
    private Integer pageNum = 1;

    @Schema(description = "每页数量")
    private Integer pageSize = 10;

    @Schema(description = "知识标题关键字")
    private String title;

    @Schema(description = "知识范围：1平台级，2场馆级，3场地级，4公告级，5FAQ")
    private Integer knowledgeScope;

    @Schema(description = "来源类型")
    private Integer sourceType;

    @Schema(description = "场馆ID")
    private Long venueId;

    @Schema(description = "场地ID")
    private Long courtId;

    @Schema(description = "是否启用：0禁用，1启用")
    private Integer enabled;

    @Schema(description = "索引状态：0未入库，1已入库")
    private Integer indexedStatus;
}
