import request from '@/utils/request'

const base_url = '/admin/order/orderInfo'

// 分页查询
export const GetOrderListByPage = (pageNum, pageSize, queryDto) => {
    return request({
        url: `${base_url}/findByPage/${pageNum}/${pageSize}`,
        method: 'post',
        data: queryDto,
    })
}

// 统计前七天的营业额金额
export const GetAllTurnover = () => {
    return request({
        url: `${base_url}/getAllTurnover`,
        method: 'get',
    })
}