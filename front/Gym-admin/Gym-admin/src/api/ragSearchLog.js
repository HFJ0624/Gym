import request from '@/utils/request'

/**
 * 根据 traceId 查询 RAG 检索日志。
 *
 * @param {string} traceId Agent 调用链ID
 */
export function getRagSearchLogsByTraceId(traceId) {
    return request({
        url: `/admin/rag/searchLog/trace/${traceId}`,
        method: 'get'
    })
}