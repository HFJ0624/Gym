package com.sau.gym.admin.mcp.service.impl;

import com.sau.gym.admin.agent.trace.AgentTraceContext;
import com.sau.gym.admin.agent.trace.AgentTraceInfo;
import com.sau.gym.admin.mcp.assistant.GymMcpAssistant;
import com.sau.gym.admin.mcp.assistant.GymMcpFileDraftAssistant;
import com.sau.gym.admin.mcp.assistant.GymMcpWriteAssistant;
import com.sau.gym.admin.mcp.config.GymMcpProperties;
import com.sau.gym.admin.mcp.model.McpFileWriteDraft;
import com.sau.gym.admin.mcp.service.GymMcpChatService;
import com.sau.gym.admin.mcp.store.McpFileWriteDraftStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 作者:hfj
 * 功能:
 * 日期: 2026/5/15 21:17
 */
@Service
public class GymMcpChatServiceImpl implements GymMcpChatService {

    private final GymMcpAssistant gymMcpAssistant;

    private final GymMcpWriteAssistant gymMcpWriteAssistant;

    private final GymMcpFileDraftAssistant fileDraftAssistant;

    private final GymMcpProperties mcpProperties;

    private final McpFileWriteDraftStore fileWriteDraftStore;

    public GymMcpChatServiceImpl(GymMcpAssistant gymMcpAssistant,
                                 GymMcpWriteAssistant gymMcpWriteAssistant,
                                 GymMcpFileDraftAssistant fileDraftAssistant,
                                 GymMcpProperties mcpProperties,
                                 McpFileWriteDraftStore fileWriteDraftStore) {
        this.gymMcpAssistant = gymMcpAssistant;
        this.gymMcpWriteAssistant = gymMcpWriteAssistant;
        this.fileDraftAssistant = fileDraftAssistant;
        this.mcpProperties = mcpProperties;
        this.fileWriteDraftStore = fileWriteDraftStore;
    }

    @Override
    public String chat(Long userId, String message) {
        if (message == null || message.trim().isEmpty()) {
            return "消息不能为空。";
        }

        String finalMessage = message.trim();

        //MCP调用也生成traceId。
        String traceId = UUID.randomUUID().toString().replace("-", "");

        try {
            //保存MCP调用链路信息
            AgentTraceContext.set(new AgentTraceInfo(traceId, userId, finalMessage));

            //1.优先处理确认等消息
            String confirmReply = handleConfirmWrite(userId, finalMessage);
            if (confirmReply != null) {
                return confirmReply;
            }

            //2.处理新建文件请求。
            if (isCreateFileRequest(finalMessage)) {
                return createFileWriteDraft(userId, finalMessage);
            }

            //普通 MCP 文档读取问答。
            String enhancedMessage = buildFileSystemDocMessage(finalMessage);

            //调用MCP Assistant
            return gymMcpAssistant.chat(userId, enhancedMessage);

        } catch (Exception e) {
            //调用失败也进行记录
            AgentTraceContext.set(new AgentTraceInfo(traceId, userId, "MCP 工具调用失败"));
            return "MCP 工具调用失败：" + e.getMessage();
        } finally {
            //清理ThreadLocal
            AgentTraceContext.clear();
        }
    }

    /**
     * 判断是否是新建文件请求。
     */
    private boolean isCreateFileRequest(String message) {
        return message.contains("新建文件")
                || message.contains("创建文件")
                || message.contains("生成文件")
                || message.contains("写入文件")
                || message.contains("新建md")
                || message.contains("生成md");
    }

    /**
     * 创建文件写入草稿。
     * 这里只生成草稿，不真正写文件。
     */
    private String createFileWriteDraft(Long userId, String message) {
        String relativePath = extractRelativePath(message);

        if (!StringUtils.hasText(relativePath)) {
            return """
                    请指定要新建的文件名，例如：
                    新建文件 xxx_xxx.md，内容：这里写入场馆补充知识
                    """;
        }

        // 校验并解析安全路径。
        Path targetPath = resolveSafeTargetPath(relativePath);

        //第一版不允许覆盖已有文件。
        if (Files.exists(targetPath)) {
            return "文件已存在，第一版不允许覆盖文件：" + targetPath;
        }

        //如果管理员明确写了“内容：xxx”，就直接使用该内容。否则调用大模型生成文件内容草稿。
        String content = extractExplicitContent(message);

        if (!StringUtils.hasText(content)) {
            content = fileDraftAssistant.generateContent("""
                    管理员想创建文件：%s
                    
                    管理员原始需求：
                    %s
                    
                    请生成适合写入该文件的正文内容。
                    """.formatted(relativePath, message));
        }

        //防止一次写入内容过大。(最大不能超过6000字)
        if (content.length() > 6000) {
            content = content.substring(0, 6000)
                    + "\n\n<!-- 内容过长，已自动截断到 6000 字符以内 -->";
        }

        String confirmToken = generateConfirmToken();

        McpFileWriteDraft draft = new McpFileWriteDraft();
        draft.setUserId(userId);
        draft.setRelativePath(relativePath);
        draft.setAbsolutePath(targetPath.toString());
        draft.setContent(content);
        draft.setConfirmToken(confirmToken);
        draft.setCreateTime(new Date());

        fileWriteDraftStore.save(userId, draft);

        return """
                已生成文件写入草稿，请确认后再写入。

                文件路径：
                %s

                内容预览：
                %s

                确认写入请输入：
                确认写入 %s

                注意：
                草稿 10 分钟内有效。
                """.formatted(
                targetPath,
                preview(content),
                confirmToken
        );
    }

    /**
     * 处理确认写入。
     */
    private String handleConfirmWrite(Long userId, String message) {
        if (!message.startsWith("确认写入")) {
            return null;
        }

        String token = message.replace("确认写入", "").trim();

        if (!StringUtils.hasText(token)) {
            return "请带上确认码，例如：确认写入 123456";
        }

        McpFileWriteDraft draft = fileWriteDraftStore.get(userId);

        if (draft == null) {
            return "当前没有待确认的文件写入草稿，或者草稿已过期。";
        }

        if (!token.equals(draft.getConfirmToken())) {
            return "确认码错误，请核对后重新输入。";
        }

        //写入前再次校验路径，防止 Redis 草稿被污染。
        Path targetPath = resolveSafeTargetPath(draft.getRelativePath());

        if (Files.exists(targetPath)) {
            fileWriteDraftStore.clear(userId);
            return "文件已存在，已取消写入，避免覆盖：" + targetPath;
        }

        //用 MCP write_file 工具执行真实写入。
        String writeCommand = """
                请调用 write_file 工具写入文件。

                path:
                %s

                content:
                %s
                """.formatted(
                targetPath.toString().replace("\\", "/"),
                draft.getContent()
        );

        String result = gymMcpWriteAssistant.writeFile(userId, writeCommand);

        fileWriteDraftStore.clear(userId);

        return """
                文件写入操作已执行。

                文件路径：
                %s

                MCP 返回：
                %s
                """.formatted(targetPath, result);
    }

    /**
     * 从管理员消息中提取相对文件路径。
     */
    private String extractRelativePath(String message) {
        Pattern pattern = Pattern.compile("([a-zA-Z0-9_\\-/\\u4e00-\\u9fa5]+\\.(md|txt|yml|yaml|sql))");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }

        return null;
    }

    /**
     * 提取管理员明确提供的内容。
     */
    private String extractExplicitContent(String message) {
        int index = message.indexOf("内容：");

        if (index >= 0) {
            return message.substring(index + "内容：".length()).trim();
        }

        index = message.indexOf("内容:");

        if (index >= 0) {
            return message.substring(index + "内容:".length()).trim();
        }

        return null;
    }

    /**
     * 解析安全目标路径。
     */
    private Path resolveSafeTargetPath(String relativePath) {
        if (!StringUtils.hasText(relativePath)) {
            throw new RuntimeException("文件路径不能为空");
        }

        //不允许管理员直接传绝对路径。
        if (relativePath.contains(":") || relativePath.startsWith("/") || relativePath.startsWith("\\")) {
            throw new RuntimeException("只允许使用相对路径，不能使用绝对路径");
        }

        //不允许路径穿越。
        if (relativePath.contains("..")) {
            throw new RuntimeException("文件路径不允许包含 ..");
        }

        Path root = Paths.get(mcpProperties.getDocsRoot())
                .toAbsolutePath()
                .normalize();

        Path target = root.resolve(relativePath)
                .toAbsolutePath()
                .normalize();

        //再次确认目标路径必须在 docs-root 内。
        if (!target.startsWith(root)) {
            throw new RuntimeException("文件只能写入 docs-root 目录下");
        }

        return target;
    }

    /**
     * 构造文件系统文档助手输入。
     */
    private String buildFileSystemDocMessage(String userMessage) {
        String docsRoot = mcpProperties.getDocsRoot();

        return """
                你现在是 Gym 后台文件系统 MCP 文档助手。
                                
                当前允许读取的文档根目录：
                %s
                
                你应该优先读取该目录下的文档来回答问题。
                
                重要限制：
                1. 如果用户只是问有哪些文件，只使用 list_directory 查看一级文件列表。
                2. 不要读取整个目录树。
                3. 不要一次读取多个文件。
                4. 每次最多读取 1 个最相关文件。
                5. 不要输出文件原文，只做摘要、归纳和解释。
                6. 单次回答控制在 800 字以内。
                7. 如果当前文档中没有相关信息，直接说明没有找到，不要编造。
                
                只读限制：
                1. 普通问答只能读取文件。
                2. 不能写文件。
                3. 不能修改文件。
                4. 不能删除文件。
                5. 不能移动文件。
                
                管理员问题：
                %s
                """.formatted(docsRoot, userMessage);
    }

    /**
     * 生成 6 位确认码。
     */
    private String generateConfirmToken() {
        return String.valueOf((int) ((Math.random() * 900000) + 100000));
    }

    /**
     * 内容预览。
     */
    private String preview(String content) {
        if (content == null) {
            return "";
        }

        if (content.length() <= 800) {
            return content;
        }

        return content.substring(0, 800) + "\n\n......内容过长，已截断预览";
    }
}
