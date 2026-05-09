package com.sau.gym.admin.rag.chunk;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者:hfj
 * 功能:简单文本切分器。
 * 日期: 2026/4/30 16:29
 */
@Component
public class SimpleTextChunker {

    /**
     * 把长文本切成多个 chunk。
     *
     * @param text      原始文本
     * @param chunkSize 每个 chunk 最大字符数
     * @param overlap   相邻 chunk 重叠字符数
     * @return chunk 列表
     */
    public List<String> split(String text, int chunkSize, int overlap) {
        List<String> chunks = new ArrayList<>();

        // 空文本直接返回空列表
        if (text == null || text.trim().isEmpty()) {
            return chunks;
        }

        // 清理多余空白，避免 embedding 被噪声影响
        String cleanText = text
                .replace("\r", "\n")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();

        // 如果文本本身很短，直接作为一个 chunk
        if (cleanText.length() <= chunkSize) {
            chunks.add(cleanText);
            return chunks;
        }

        int start = 0;

        while (start < cleanText.length()) {
            int end = Math.min(start + chunkSize, cleanText.length());

            // 截取当前 chunk
            String chunk = cleanText.substring(start, end).trim();

            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }

            // 已经到结尾，退出
            if (end >= cleanText.length()) {
                break;
            }

            //下一段向前重叠overlap字符,避免上下文断裂
            start = end - overlap;

            // 防止overlap设置异常导致死循环
            if (start < 0) {
                start = end;
            }
        }

        return chunks;
    }
}
