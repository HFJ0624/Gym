// 前台订单 mock（localStorage）
import request from '@/utils/frontRequest'

// 获取我的预约列表
export function getCourtOrder(userId, pageNum, pageSize) {
  return request({
    url: `/front/order/${userId}/${pageNum}/${pageSize}`,
    method: 'get',
  })
}

// 取消预约
export function cancelOrder(orderId) {
  return request({
    url: '/front/order/cancel',
    method: 'post',
    params: { orderId }
  })
}

