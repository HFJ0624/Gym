import request from '@/utils/request'

export function getRagEvalCasePage(params) {
    return request({
        url: '/admin/ai/rag/eval/case/page',
        method: 'get',
        params
    })
}

export function saveRagEvalCase(data) {
    return request({
        url: '/admin/ai/rag/eval/case/save',
        method: 'post',
        data
    })
}

export function deleteRagEvalCase(id) {
    return request({
        url: `/admin/ai/rag/eval/case/${id}`,
        method: 'delete'
    })
}

export function runRagEval(data) {
    return request({
        url: '/admin/ai/rag/eval/run',
        method: 'post',
        data
    })
}

export function getRagEvalRunPage(params) {
    return request({
        url: '/admin/ai/rag/eval/run/page',
        method: 'get',
        params
    })
}

export function getRagEvalRunDetail(runId) {
    return request({
        url: `/admin/ai/rag/eval/run/detail/${runId}`,
        method: 'get'
    })
}