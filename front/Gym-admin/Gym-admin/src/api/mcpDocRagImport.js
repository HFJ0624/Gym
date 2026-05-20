import request from '@/utils/request'

/**
 * 查询 MCP docs-root 下的文件。
 */
export function getMcpDocFiles() {
    return request({
        url: '/admin/ai/rag/mcpDoc/files',
        method: 'get'
    })
}

/**
 * 预览文件章节。
 *
 * @param {string} relativePath 相对路径
 */
export function previewMcpDoc(relativePath) {
    return request({
        url: '/admin/ai/rag/mcpDoc/preview',
        method: 'get',
        params: {
            relativePath
        }
    })
}

/**
 * 导入选中的章节到 RAG。
 *
 * @param {Object} data 导入参数
 */
export function importMcpDocToRag(data) {
    return request({
        url: '/admin/ai/rag/mcpDoc/import',
        method: 'post',
        data,

        /**
         * 导入会调用 embedding 模型，可能比普通接口慢。
         */
        timeout: 120000
    })
}