import request from '@/utils/request'

const base_url = '/admin/stadium/court'

// 分页查询
export const GetCourtListByPage = (pageNum, pageSize, queryDto) => {
    return request({
        url: `${base_url}/findByPage/${pageNum}/${pageSize}`,
        method: 'post',
        data: queryDto,
    })
}

// 添加场地请求方法
export const saveCourt = (data) => {
    return request({
        url: `${base_url}/saveCourt`,
        method: 'post',
        data
    })
}

// 修改场地
export const updateCourt = (data) => {
    return request({
        url: `${base_url}/updateCourt`,
        method: 'put',
        data
    })
}

// 删除场地
export const DeleteCourtById = (courtId) => {
    return request({
        url: `${base_url}/deleteById/${courtId}`,
        method: 'delete'
    })
}