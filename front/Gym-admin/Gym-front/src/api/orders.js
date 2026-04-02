// 前台订单 mock（localStorage）
import request from '@/utils/frontRequest'

// 获取我的预约列表
export function getCourtOrder(userId) {
  return request({
    url: '/front/order',
    method: 'get',
    params: { userId }
  })
}

