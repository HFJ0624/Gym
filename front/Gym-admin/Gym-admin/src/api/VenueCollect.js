import request from '@/utils/request'

const base_url = '/admin/visit/collectVenue'

// 分页查询
export const GetVenueCollectListByPage = (pageNum, pageSize, queryDto) => {
    return request({
        url: `${base_url}/findByPage/${pageNum}/${pageSize}`,
        method: 'post',
        data: queryDto,
    })
}