package com.sau.gym.admin.rag.service.impl;

import com.sau.gym.admin.mapper.McpDocImportMapper;
import com.sau.gym.admin.mapper.McpKnowledgeDocumentMapper;
import com.sau.gym.admin.mcp.config.GymMcpProperties;
import com.sau.gym.admin.rag.service.McpDocRagImportService;
import com.sau.gym.model.dto.rag.McpDocImportDto;
import com.sau.gym.model.entity.rag.KnowledgeDocument;
import com.sau.gym.model.entity.rag.RagImportMcpDoc;
import com.sau.gym.model.vo.rag.McpDocFileVO;
import com.sau.gym.model.vo.rag.McpDocImportResultVO;
import com.sau.gym.model.vo.rag.McpDocSectionVO;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 作者:hfj
 * 功能:MCP文档导入RAG服务实现
 * 日期: 2026/5/20 8:39
 */
@Service
public class McpDocRagImportServiceImpl implements McpDocRagImportService {

    private final GymMcpProperties mcpProperties;

    private final EmbeddingModel embeddingModel;

    private final EmbeddingStore<TextSegment> embeddingStore;

    private final McpDocImportMapper mcpDocImportMapper;

    private final McpKnowledgeDocumentMapper mcpKnowledgeDocumentMapper;

    public McpDocRagImportServiceImpl(GymMcpProperties mcpProperties,
                                      EmbeddingModel embeddingModel,
                                      EmbeddingStore<TextSegment> embeddingStore,
                                      McpDocImportMapper mcpDocImportMapper,
                                      McpKnowledgeDocumentMapper mcpKnowledgeDocumentMapper) {
        this.mcpProperties = mcpProperties;
        this.embeddingModel = embeddingModel;
        this.embeddingStore = embeddingStore;
        this.mcpDocImportMapper = mcpDocImportMapper;
        this.mcpKnowledgeDocumentMapper = mcpKnowledgeDocumentMapper;
    }

    /**
     * 查询 docs-root 下可导入的 Markdown / 文本文件。
     */
    @Override
    public List<McpDocFileVO> listFiles() {
        Path root = getDocsRoot();

        if (!Files.exists(root)) {
            return new ArrayList<>();
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            return Files.walk(root)
                    .filter(Files::isRegularFile)
                    .filter(this::isSupportedFile)
                    .map(path -> {
                        try {
                            McpDocFileVO vo = new McpDocFileVO();

                            vo.setFileName(path.getFileName().toString());
                            vo.setRelativePath(root.relativize(path).toString().replace("\\", "/"));
                            vo.setSize(Files.size(path));

                            String lastModified = Files.getLastModifiedTime(path)
                                    .toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .format(formatter);

                            vo.setLastModifiedTime(lastModified);

                            return vo;
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            throw new RuntimeException("读取 MCP 文档目录失败：" + e.getMessage(), e);
        }
    }

    /**
     * 预览 Markdown 章节。
     */
    @Override
    public List<McpDocSectionVO> previewMarkdown(String relativePath) {
        Path filePath = resolveSafePath(relativePath);

        if (!Files.exists(filePath)) {
            throw new RuntimeException("文件不存在：" + relativePath);
        }

        try {
            String content = Files.readString(filePath, StandardCharsets.UTF_8);

            return splitMarkdownToSections(content);

        } catch (Exception e) {
            throw new RuntimeException("预览 Markdown 文件失败：" + e.getMessage(), e);
        }
    }

    /**
     * 导入管理员选中的章节到 RAG 向量库。
     */
    @Transactional
    @Override
    public McpDocImportResultVO importSelectedSections(Long userId, McpDocImportDto dto) {
        if (dto == null) {
            throw new RuntimeException("导入参数不能为空");
        }

        if (!StringUtils.hasText(dto.getRelativePath())) {
            throw new RuntimeException("文件路径不能为空");
        }

        if (dto.getSectionIds() == null || dto.getSectionIds().isEmpty()) {
            throw new RuntimeException("请选择要导入的章节");
        }

        Path filePath = resolveSafePath(dto.getRelativePath());

        if (!Files.exists(filePath)) {
            throw new RuntimeException("文件不存在：" + dto.getRelativePath());
        }

        try {

            //后端重新读取文件并重新切章节。不能信任前端传回来的 content。
            List<McpDocSectionVO> allSections = previewMarkdown(dto.getRelativePath());

            Set<String> selectedIds = new HashSet<>(dto.getSectionIds());

            List<McpDocSectionVO> selectedSections = allSections.stream()
                    .filter(section -> selectedIds.contains(section.getSectionId()))
                    .collect(Collectors.toList());

            /*
             * 1. 先把选中的章节合并成一份完整知识内容。
             *
             * 这个内容会写入 MySQL 的 knowledge_document 表。
             * 后台 RAG 知识库管理页面展示的就是这张表。
             */
            String documentContent = selectedSections.stream()
                    .map(section -> section.getContent() == null ? "" : section.getContent())
                    .collect(Collectors.joining("\n\n"));

            //2. 插入 knowledge_document 原始知识表。
            KnowledgeDocument document = new KnowledgeDocument();

            document.setTitle(StringUtils.hasText(dto.getTitle()) ? dto.getTitle() : filePath.getFileName().toString());
            document.setContent(documentContent);
            document.setKnowledgeScope(2);
            document.setSourceType(2);
            document.setTopic(StringUtils.hasText(dto.getCategory()) ? dto.getCategory() : "MCP文档");
            document.setPriority(5);
            document.setEnabled(1);
            document.setIndexedStatus(0);
            mcpKnowledgeDocumentMapper.insertMcpKnowledgeDocument(document);

            Long documentId = document.getId();

            if (documentId == null) {
                throw new RuntimeException("插入 knowledge_document 失败，未获取到文档ID");
            }

            if (selectedSections.isEmpty()) {
                throw new RuntimeException("没有找到选中的章节");
            }

            //把章节切成 RAG chunk。
            List<TextSegment> textSegments = new ArrayList<>();

            String fileName = filePath.getFileName().toString();

            for (McpDocSectionVO section : selectedSections) {
                List<String> chunks = splitTextToChunks(section.getContent(), 800, 120);

                for (int i = 0; i < chunks.size(); i++) {
                    String chunk = chunks.get(i);

                    //metadata展示命中来源
                    Metadata metadata = Metadata.from("source_type", "mcp_md");

                    metadata.put("doc_id", String.valueOf(documentId));
                    metadata.put("document_id", String.valueOf(documentId));
                    metadata.put("file_name", fileName);
                    metadata.put("relative_path", dto.getRelativePath());
                    metadata.put("section_id", section.getSectionId());
                    metadata.put("section_title", section.getTitle());
                    metadata.put("category", dto.getCategory() == null ? "" : dto.getCategory());
                    metadata.put("title", StringUtils.hasText(dto.getTitle()) ? dto.getTitle() : fileName);
                    metadata.put("chunk_index", String.valueOf(i));

                    textSegments.add(TextSegment.from(chunk, metadata));
                }
            }

            if (textSegments.isEmpty()) {
                throw new RuntimeException("没有可导入的文本内容");
            }

            //调用 embedding 模型生成向量。
            List<Embedding> embeddings = embeddingModel.embedAll(textSegments).content();

            //写入 pgvector。
            embeddingStore.addAll(embeddings, textSegments);

            //更新RAG知识库
            mcpKnowledgeDocumentMapper.updateIndexedStatus(documentId, 1);
            RagImportMcpDoc ragImportMcpDoc = new RagImportMcpDoc();
            ragImportMcpDoc.setFileName(fileName);
            ragImportMcpDoc.setRelativePath(dto.getRelativePath());
            ragImportMcpDoc.setTitle(StringUtils.hasText(dto.getTitle()) ? dto.getTitle() : fileName);
            ragImportMcpDoc.setCategory(dto.getCategory());
            ragImportMcpDoc.setSectionCount(selectedSections.size());
            ragImportMcpDoc.setChunkCount(textSegments.size());
            ragImportMcpDoc.setStatus("SUCCESS");
            ragImportMcpDoc.setErrorMessage(null);
            ragImportMcpDoc.setCreateBy(userId);
            //写入导入记录，方便后台追踪。
            mcpDocImportMapper.insertImportRecord(ragImportMcpDoc);

            McpDocImportResultVO resultVO = new McpDocImportResultVO();
            resultVO.setImportId(null);
            resultVO.setSectionCount(selectedSections.size());
            resultVO.setChunkCount(textSegments.size());
            resultVO.setMessage("导入成功，已写入 RAG 向量库");

            return resultVO;

        } catch (Exception e) {
            RagImportMcpDoc ragImportMcpDoc = new RagImportMcpDoc();
            ragImportMcpDoc.setFileName(filePath.getFileName().toString());
            ragImportMcpDoc.setRelativePath(dto.getRelativePath());
            ragImportMcpDoc.setTitle(dto.getTitle());
            ragImportMcpDoc.setCategory(dto.getCategory());
            ragImportMcpDoc.setSectionCount(0);
            ragImportMcpDoc.setChunkCount(0);
            ragImportMcpDoc.setStatus("FAILED");
            ragImportMcpDoc.setErrorMessage(e.getMessage());
            ragImportMcpDoc.setCreateBy(userId);
            //失败也记录一下。
            mcpDocImportMapper.insertImportRecord(ragImportMcpDoc);

            throw new RuntimeException("导入 RAG 失败：" + e.getMessage(), e);
        }
    }

    /**
     * 获取 docs-root 目录。
     */
    private Path getDocsRoot() {
        if (!StringUtils.hasText(mcpProperties.getDocsRoot())) {
            throw new RuntimeException("docs-root 未配置");
        }

        return Paths.get(mcpProperties.getDocsRoot())
                .toAbsolutePath()
                .normalize();
    }

    /**
     * 解析安全路径。
     */
    private Path resolveSafePath(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            throw new RuntimeException("文件路径不能为空");
        }

        if (relativePath.contains(":") || relativePath.startsWith("/") || relativePath.startsWith("\\")) {
            throw new RuntimeException("只允许使用相对路径");
        }

        if (relativePath.contains("..")) {
            throw new RuntimeException("文件路径不允许包含 ..");
        }

        Path root = getDocsRoot();

        Path target = root.resolve(relativePath)
                .toAbsolutePath()
                .normalize();

        if (!target.startsWith(root)) {
            throw new RuntimeException("只能读取 docs-root 目录下的文件");
        }

        return target;
    }

    /**
     * 判断是否支持导入。
     */
    private boolean isSupportedFile(Path path) {
        String name = path.getFileName().toString().toLowerCase();

        return name.endsWith(".md")
                || name.endsWith(".txt")
                || name.endsWith(".yml")
                || name.endsWith(".yaml")
                || name.endsWith(".sql");
    }

    /**
     * 将 Markdown 文本按标题拆分成章节。
     *
     * 标题规则：
     * # 一级标题
     * ## 二级标题
     * ### 三级标题
     */
    private List<McpDocSectionVO> splitMarkdownToSections(String content) {
        List<McpDocSectionVO> result = new ArrayList<>();

        if (!StringUtils.hasText(content)) {
            return result;
        }

        Pattern headingPattern = Pattern.compile("^(#{1,6})\\s+(.+)$");

        String[] lines = content.split("\\r?\\n");

        String currentTitle = "全文";
        StringBuilder currentContent = new StringBuilder();

        int sectionIndex = 0;
        boolean hasHeading = false;

        for (String line : lines) {
            Matcher matcher = headingPattern.matcher(line);

            if (matcher.find()) {
                hasHeading = true;

                //遇到新标题时，先保存上一个章节。
                if (currentContent.length() > 0) {
                    result.add(buildSection("s" + sectionIndex, currentTitle, currentContent.toString()));
                    sectionIndex++;
                    currentContent.setLength(0);
                }

                currentTitle = matcher.group(2).trim();
                currentContent.append(line).append("\n");
            } else {
                currentContent.append(line).append("\n");
            }
        }

        //保存最后一个章节。
        if (currentContent.length() > 0) {
            result.add(buildSection("s" + sectionIndex, currentTitle, currentContent.toString()));
        }

        //如果完全没有 Markdown 标题，则整个文件作为一个章节。
        if (!hasHeading && result.isEmpty()) {
            result.add(buildSection("s0", "全文", content));
        }

        return result;
    }

    /**
     * 构造章节 VO。
     */
    private McpDocSectionVO buildSection(String sectionId, String title, String content) {
        McpDocSectionVO vo = new McpDocSectionVO();

        vo.setSectionId(sectionId);
        vo.setTitle(title);
        vo.setContent(content);
        vo.setPreview(preview(content, 260));
        vo.setLength(content == null ? 0 : content.length());

        return vo;
    }

    /**
     * 简单字符切块。
     *
     * 第一版用字符长度切块即可。
     * 后续如果你要优化，可以换成 LangChain4j 的 DocumentSplitter。
     */
    private List<String> splitTextToChunks(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();

        if (!StringUtils.hasText(text)) {
            return chunks;
        }

        int start = 0;
        int length = text.length();

        while (start < length) {
            int end = Math.min(start + chunkSize, length);

            String chunk = text.substring(start, end).trim();

            if (StringUtils.hasText(chunk)) {
                chunks.add(chunk);
            }

            if (end >= length) {
                break;
            }

            start = Math.max(0, end - overlap);
        }

        return chunks;
    }

    /**
     * 内容预览。
     */
    private String preview(String text, int maxLength) {
        if (text == null) {
            return "";
        }

        if (text.length() <= maxLength) {
            return text;
        }

        return text.substring(0, maxLength) + "...";
    }
}
