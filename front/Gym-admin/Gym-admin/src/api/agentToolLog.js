import request from '@/utils/request'

/**
 * 分页查询 Agent 工具调用日志。
 */
export function pageAgentToolLog(data) {
    return request({
        url: '/admin/ai/toolLog/page',
        method: 'post',
        data
    })
}

/**
 * 查询 Agent 工具调用日志详情。
 */
export function getAgentToolLogDetail(id) {
    return request({
        url: `/admin/ai/toolLog/${id}`,
        method: 'get'
    })
}

/**
 * 查询 Agent 工具调用统计。
 */
export function getAgentToolLogStats(data) {
    return request({
        url: '/admin/ai/toolLog/stats',
        method: 'post',
        data
    })
}