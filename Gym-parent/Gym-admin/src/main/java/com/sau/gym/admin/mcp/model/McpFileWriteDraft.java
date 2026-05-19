package com.sau.gym.admin.mcp.model;

import lombok.Data;

import java.util.Date;

/**
 * 作者:hfj
 * 功能:MCP 文件写入草稿
 * 日期: 2026/5/19 10:06
 */
@Data
public class McpFileWriteDraft {

    /**
     * 管理员用户ID。
     */
    private Long userId;

    /**
     * 相对文件路径。
     *
     * 示例：
     * venue-extra.md
     * docs/booking-rule.md
     *
     * 注意：
     * 不允许绝对路径。
     */
    private String relativePath;

    /**
     * 最终写入的绝对路径。
     *
     * 示例：
     * D:/workspace/gym-mcp-docs/venue-extra.md
     */
    private String absolutePath;

    /**
     * 文件内容。
     */
    private String content;

    /**
     * 确认码。
     */
    private String confirmToken;

    /**
     * 创建时间。
     */
    private Date createTime;
}
