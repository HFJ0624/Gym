import request from '@/utils/frontRequest'

// 获得所有场馆
export const GetAllCourt = (venueId) => {
    return request({
        url: `/front/venues/court/${venueId}`,
        method: 'get',
    })
}

// 获取场地详情
export function GetCourtDetail(courtId, venueId) {
    return request({
        url: `/front/venues/details/${courtId}`,
        method: 'get',
        params: { venueId }
    })
}

// 提交场地预约
export function BookCourt(bookingData) {
    return request({
        url: '/front/venues/book',
        method: 'post',
        data: bookingData
    })
}

/**
 * 查询某个场地某天的可预约时段。
 */
export function getAvailableSlots(params) {
    return request({
        url: '/front/courtBook/availableSlots',
        method: 'get',
        params
    })
}