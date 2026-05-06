package com.sau.gym.model.vo.rag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/6 14:35
 */
@Data
@Schema(description = "RAG单条知识索引结果")
public class RagIndexResultVO {

    @Schema(description = "知识ID")
    private Long docId;

    @Schema(description = "知识标题")
    private String title;

    @Schema(description = "删除的旧向量数量")
    private Integer deletedCount;

    @Schema(description = "新写入的chunk数量")
    private Integer indexedChunkCount;

    @Schema(description = "是否成功")
    private Boolean success;

    @Schema(description = "提示信息")
    private String message;
}
