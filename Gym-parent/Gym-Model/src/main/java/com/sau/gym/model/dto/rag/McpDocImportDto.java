package com.sau.gym.model.dto.rag;

import lombok.Data;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/20 8:29
 */
@Data
public class McpDocImportDto {

    /**
     * 要导入的文档相对路径。
     */
    private String relativePath;

    /**
     * 选择导入的章节ID列表。
     */
    private List<String> sectionIds;

    /**
     * 导入标题。
     */
    private String title;

    /**
     * 分类。
     */
    private String category;
}
