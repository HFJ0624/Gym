import request from '@/utils/frontRequest'

// 分页获取场馆评论
export const GetVenueCommentListByPage = (venueId, pageNum, pageSize) => {
    return request({
        url: `/front/venues/findByPageComment/${venueId}/${pageNum}/${pageSize}`,
        method: 'post',
    })
}

// 提交场馆评论
export const SubmitVenueComment = (data) => {
    return request({
        url: `/front/venues/saveVenueComment`,
        method: 'post',
        data
    })
}
