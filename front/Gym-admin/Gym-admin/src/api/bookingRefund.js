import request from '@/utils/request'

/**
 * 分页查询退款申请
 */
export function getRefundPage(params) {
    return request({
        url: '/admin/booking/refund/page',
        method: 'get',
        params
    })
}

/**
 * 审核通过
 */
export function approveRefund(data) {
    return request({
        url: '/admin/booking/refund/approve',
        method: 'post',
        data
    })
}

/**
 * 审核拒绝
 */
export function rejectRefund(data) {
    return request({
        url: '/admin/booking/refund/reject',
        method: 'post',
        data
    })
}