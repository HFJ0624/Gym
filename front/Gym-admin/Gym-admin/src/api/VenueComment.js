import request from '@/utils/request'

const base_url = '/admin/bulletin/venueComment'

// 分页查询
export const GetVenueCommentListByPage = (pageNum, pageSize, queryDto) => {
    return request({
        url: `${base_url}/findByPage/${pageNum}/${pageSize}`,
        method: 'post',
        data: queryDto,
    })
}

// 添加场馆评论
export const SaveVenueComment = (data) => {
    return request({
        url: `${base_url}/saveVenueComment`,
        method: 'post',
        data
    })
}

// 修改场馆评论
export const UpdateVenueComment = (data) => {
    return request({
        url: `${base_url}/updateVenueComment`,
        method: 'put',
        data
    })
}

// 删除场馆评论
export const DeleteVenueCommentById = (venueCommentId) => {   
    return request({
        url: `${base_url}/deleteById/${venueCommentId}`,
        method: 'delete'
    })
}