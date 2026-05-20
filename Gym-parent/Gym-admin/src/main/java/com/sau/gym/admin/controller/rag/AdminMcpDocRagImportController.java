package com.sau.gym.admin.controller.rag;

import com.sau.gym.admin.rag.service.McpDocRagImportService;
import com.sau.gym.model.dto.rag.McpDocImportDto;
import com.sau.gym.model.entity.base.Result;
import com.sau.gym.model.entity.base.ResultCodeEnum;
import com.sau.gym.model.entity.user.User;
import com.sau.gym.model.vo.rag.McpDocFileVO;
import com.sau.gym.model.vo.rag.McpDocImportResultVO;
import com.sau.gym.model.vo.rag.McpDocSectionVO;
import com.sau.gym.utils.AuthContextUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/20 8:46
 */
@RestController
@RequestMapping("/admin/ai/rag/mcpDoc")
public class AdminMcpDocRagImportController {

    private final McpDocRagImportService mcpDocRagImportService;

    public AdminMcpDocRagImportController(McpDocRagImportService mcpDocRagImportService) {
        this.mcpDocRagImportService = mcpDocRagImportService;
    }

    /**
     * 查询 docs-root 下可导入的文件。
     */
    @GetMapping("/files")
    public Result files() {
        List<McpDocFileVO> list = mcpDocRagImportService.listFiles();

        return Result.build(list, ResultCodeEnum.SUCCESS);
    }

    /**
     * 预览 Markdown 文件章节。
     */
    @GetMapping("/preview")
    public Result preview(@RequestParam String relativePath) {
        List<McpDocSectionVO> sections = mcpDocRagImportService.previewMarkdown(relativePath);

        return Result.build(sections, ResultCodeEnum.SUCCESS);
    }

    /**
     * 导入选中的章节到 RAG。
     */
    @PostMapping("/import")
    public Result importToRag(@RequestBody McpDocImportDto dto) {
        User user = AuthContextUtil.get();

        Long userId = user.getId();

        McpDocImportResultVO result = mcpDocRagImportService.importSelectedSections(userId, dto);
        return Result.build(result, ResultCodeEnum.SUCCESS);
    }
}
