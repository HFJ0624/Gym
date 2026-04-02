import request from '@/utils/request'

const base_url = '/admin/stadium/venue'

// 分页查询
export const GetVenueListByPage = (pageNum, pageSize, queryDto) => {
    return request({
        url: `${base_url}/findByPage/${pageNum}/${pageSize}`,
        method: 'post',
        data: queryDto,
    })
}

// 添加场馆请求方法
export const SaveVenue = (data) => {
    return request({
        url: `${base_url}/saveVenue`,
        method: 'post',
        data
    })
}

// 修改场馆
export const UpdateVenue = (data) => {
    return request({
        url: `${base_url}/updateVenue`,
        method: 'put',
        data
    })
}

// 删除场馆
export const DeleteVenueById = (venueId) => {
    return request({
        url: `${base_url}/deleteById/${venueId}`,
        method: 'delete'
    })
}

// 获得所有场馆
export const GetAllVenue = () => {
    return request({
        url: `${base_url}/findAllVenue`,
        method: 'get'
    })
}