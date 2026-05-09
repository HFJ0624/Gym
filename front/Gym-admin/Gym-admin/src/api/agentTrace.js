import request from '@/utils/request'

/**
 * 分页查询 Agent Trace 调用链
 *
 * @param {Object} params 查询参数
 * @param {number} params.current 当前页
 * @param {number} params.limit 每页条数
 * @param {number|string} params.userId 用户ID
 * @param {string} params.status 状态：RUNNING / SUCCESS / FAILED
 * @param {string} params.keyword 关键词：traceId / 用户输入 / 回复内容
 */
export function getAgentTracePage(params) {
    return request({
        url: '/admin/agent/trace/page',
        method: 'get',
        params
    })
}

/**
 * 查询 Agent Trace 详情
 *
 * @param {string} traceId 调用链ID
 */
export function getAgentTraceDetail(traceId) {
    return request({
        url: `/admin/agent/trace/detail/${traceId}`,
        method: 'get'
    })
}

/**
 * 查询 Agent Trace 步骤
 *
 * 第一版详情接口已经返回 trace + steps，
 * 这个接口可以先备用。
 *
 * @param {string} traceId 调用链ID
 */
export function getAgentTraceSteps(traceId) {
    return request({
        url: `/admin/agent/trace/steps/${traceId}`,
        method: 'get'
    })
}