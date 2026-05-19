package com.sau.gym.admin.mcp.store;

import com.sau.gym.admin.mcp.model.McpFileWriteDraft;

/**
 * MCP 文件写入草稿存储
 */
public interface McpFileWriteDraftStore {

    /**
     * 保存草稿。
     */
    void save(Long userId, McpFileWriteDraft draft);

    /**
     * 获取草稿。
     */
    McpFileWriteDraft get(Long userId);

    /**
     * 删除草稿。
     */
    void clear(Long userId);
}
