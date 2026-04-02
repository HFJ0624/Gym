import request from '@/utils/request'

const base_url = '/admin/booking/venueBook'

// 分页查询
export const GetVenueBookListByPage = (pageNum, pageSize, queryDto) => {
    return request({
        url: `${base_url}/findByPage/${pageNum}/${pageSize}`,
        method: 'post',
        data: queryDto,
    })
}

// 根据id删除预约场地
export const DeleteVenueBookById = (id) => {
    return request({
        url: `${base_url}/deleteById/${id}`,
        method: 'delete'
    })
}