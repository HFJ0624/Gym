package com.sau.gym.admin.rag.service;

import com.sau.gym.model.dto.rag.McpDocImportDto;
import com.sau.gym.model.vo.rag.McpDocFileVO;
import com.sau.gym.model.vo.rag.McpDocImportResultVO;
import com.sau.gym.model.vo.rag.McpDocSectionVO;

import java.util.List;

public interface McpDocRagImportService {

    /**
     * 查询 docs-root 下可导入的文档文件。
     */
    List<McpDocFileVO> listFiles();

    /**
     * 预览 Markdown 文档章节。
     */
    List<McpDocSectionVO> previewMarkdown(String relativePath);

    /**
     * 导入选中的 Markdown 章节到 RAG 向量库。
     */
    McpDocImportResultVO importSelectedSections(Long userId, McpDocImportDto dto);
}
