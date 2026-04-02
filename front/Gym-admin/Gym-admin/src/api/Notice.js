import request from '@/utils/request'

const base_url = '/admin/bulletin/notice'

// 分页查询
export const GetNoticeListByPage = (pageNum, pageSize, queryDto) => {
    return request({
        url: `${base_url}/findByPage/${pageNum}/${pageSize}`,
        method: 'post',
        data: queryDto,
    })
}

// 添加公告请求方法
export const SaveNotice = (data) => {
    return request({
        url: `${base_url}/saveNotice`,
        method: 'post',
        data
    })
}

// 修改公告
export const UpdateNotice = (data) => {
    return request({
        url: `${base_url}/updateNotice`,
        method: 'put',
        data
    })
}

// 删除公告
export const DeleteNoticeById = (noticeId) => {   
    return request({
        url: `${base_url}/deleteById/${noticeId}`,
        method: 'delete'
    })
}

// 查找所有标题
export const FindAllNotice = () => {
    return request({
        url: `${base_url}/findAllNotice`,
        method: 'get',
    })
}