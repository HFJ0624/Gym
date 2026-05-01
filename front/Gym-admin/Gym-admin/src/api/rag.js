import request from '@/utils/request'

/**
 * 分页查询 RAG 知识文档。
 */
export function pageKnowledgeDocument(data) {
    return request({
        url: '/admin/rag/document/page',
        method: 'post',
        data
    })
}

/**
 * 查询知识详情。
 */
export function getKnowledgeDocumentDetail(id) {
    return request({
        url: `/admin/rag/document/${id}`,
        method: 'get'
    })
}

/**
 * 新增知识文档。
 */
export function saveKnowledgeDocument(data) {
    return request({
        url: '/admin/rag/document',
        method: 'post',
        data
    })
}

/**
 * 更新知识文档。
 */
export function updateKnowledgeDocument(data) {
    return request({
        url: '/admin/rag/document',
        method: 'put',
        data
    })
}

/**
 * 启用/禁用知识。
 */
export function updateKnowledgeDocumentEnabled(id, enabled) {
    return request({
        url: `/admin/rag/document/${id}/enabled/${enabled}`,
        method: 'put'
    })
}

/**
 * 删除知识文档。
 */
export function deleteKnowledgeDocument(id) {
    return request({
        url: `/admin/rag/document/${id}`,
        method: 'delete'
    })
}

/**
 * 重建 RAG 知识库索引。
 */
export function rebuildRagKnowledge() {
    return request({
        url: '/admin/rag/rebuild',
        method: 'post'
    })
}

/**
 * RAG 测试问答。
 */
export function testRagAsk(data) {
    return request({
        url: '/front/rag/ask',
        method: 'post',
        data
    })
}