package com.sau.gym.model.vo.rag;

import lombok.Data;

/**
 * 作者:hfj
 * 功能:MCP 文档文件 VO
 * 日期: 2026/5/20 8:27
 */
@Data
public class McpDocFileVO {

    /**
     * 文件名。
     */
    private String fileName;

    /**
     * 相对路径。
     */
    private String relativePath;

    /**
     * 文件大小，单位 byte。
     */
    private Long size;

    /**
     * 最后修改时间。
     */
    private String lastModifiedTime;
}
