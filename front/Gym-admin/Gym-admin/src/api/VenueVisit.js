import request from '@/utils/request'

const base_url = '/admin/visit/visitVenue'

// 分页查询
export const GetVenueVisitListByPage = (pageNum, pageSize) => {
    return request({
        url: `${base_url}/findByPage/${pageNum}/${pageSize}`,
        method: 'post',
    })
}