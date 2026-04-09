import request from '@/utils/frontRequest'

// 修改用户信息
export const UpdateUser = (queryDto) => {
    return request({
        url: `/front/updateProfile`,
        method: 'post',
        data: queryDto
    })
}

// 获取用户余额
export const GetBalance = (id) => {
    return request({
        url: `/front/getBalance`,
        method: 'get',
        params: { id }
    })
}

// 用户充值
export const Recharge = (data) => {
    return request({
        url: `/front/recharge`,
        method: 'post',
        data: data
    })
}
