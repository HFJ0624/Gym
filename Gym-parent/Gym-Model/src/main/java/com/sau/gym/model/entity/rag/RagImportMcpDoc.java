package com.sau.gym.model.entity.rag;

import lombok.Data;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/20 9:15
 */
@Data
public class RagImportMcpDoc {
    // 数据库自增主键
    private Long id;
    private String fileName;
    private String title;
    private String category;
    private Long createBy;
    private Integer sectionCount;
    private Integer chunkCount;
    private String status;
    private String errorMessage;
    private String relativePath;
}
