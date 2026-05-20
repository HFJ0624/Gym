package com.sau.gym.model.vo.rag;

import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/20 8:30
 */
@Data
public class McpDocImportResultVO {

    /**
     * 导入记录ID。
     */
    private Long importId;

    /**
     * 导入章节数。
     */
    private Integer sectionCount;

    /**
     * 写入向量库的 chunk 数量。
     */
    private Integer chunkCount;

    /**
     * 提示信息。
     */
    private String message;
}
