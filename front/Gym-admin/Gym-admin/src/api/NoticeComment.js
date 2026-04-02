import request from '@/utils/request'

const base_url = '/admin/bulletin/noticeComment'

// 分页查询
export const GetNoticeCommentListByPage = (pageNum, pageSize, queryDto) => {
    return request({
        url: `${base_url}/findByPage/${pageNum}/${pageSize}`,
        method: 'post',
        data: queryDto,
    })
}

// 添加公告评论
export const SaveNoticeComment = (data) => {
    return request({
        url: `${base_url}/saveNoticeComment`,
        method: 'post',
        data
    })
}

// 修改公告评论
export const UpdateNoticeComment = (data) => {
    return request({
        url: `${base_url}/updateNoticeComment`,
        method: 'put',
        data
    })
}

// 删除公告评论
export const DeleteNoticeCommentById = (noticeCommentId) => {   
    return request({
        url: `${base_url}/deleteById/${noticeCommentId}`,
        method: 'delete'
    })
}

// 获取最新的五条评论
export const GetRecentComment = () => {
    return request({
        url: `${base_url}/getRecentComment`,
        method: 'get'
    })
}