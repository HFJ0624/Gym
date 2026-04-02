import request from '@/utils/frontRequest'

// 获得所有公告（不分页）
export const GetAllNotice = () => {
    return request({
        url: `/front/notice`,
        method: 'get',
    })
}


// 查看公告的详情信息
export const GetNoticeDetail = (noticeId) => {
    return request({
        url: `/front/notice/${noticeId}`,
        method: 'get',
    })
}

// 提交评论
// 后端接口路径：POST /front/notice/comment
// 请求参数：
// {
//   "noticeId": 1,        // 公告ID（必填）
//   "userId": 1,          // 用户ID（必填）
//   "content": "评论内容",   // 评论内容（必填）
//   "esteem": 5          // 评分，1-5（必填）：5=好评, 4=良好, 3=中等, 2=一般, 1=差
// }
export const SubmitComment = (data) => {
    return request({
        url: `/front/notice/comment`,
        method: 'post',
        data
    })
}