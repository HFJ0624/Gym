import request from '@/utils/frontRequest'

// 我的通知列表
export function getMyNotifications(pageNum, pageSize, readStatus) {
    return request({
        url: `/front/notification/my/${pageNum}/${pageSize}`,
        method: 'get',
        params: {
            readStatus
        }
    })
}

// 未读通知数量
export function getUnreadNotificationCount() {
    return request({
        url: '/front/notification/unread/count',
        method: 'get'
    })
}

// 标记单条已读
export function markNotificationRead(id) {
    return request({
        url: `/front/notification/read/${id}`,
        method: 'post'
    })
}

// 全部标记已读
export function markAllNotificationsRead() {
    return request({
        url: '/front/notification/read/all',
        method: 'post'
    })
}

// 删除通知
export function deleteNotification(id) {
    return request({
        url: `/front/notification/${id}`,
        method: 'delete'
    })
}