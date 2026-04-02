import request from '@/utils/frontRequest'

// 修改用户信息
export const UpdateUser = (queryDto) => {
    return request({
        url: `/front/updateProfile`,
        method: 'post',
        data: queryDto
    })
}