import request from '@/utils/request'

const base_url = '/admin/booking/courtBook'

// 分页查询
export const GetCourtBookListByPage = (pageNum, pageSize, queryDto) => {
    return request({
        url: `${base_url}/findByPage/${pageNum}/${pageSize}`,
        method: 'post',
        data: queryDto,
    })
}

// 根据id删除预约场地
export const DeleteCourtBookById = (id) => {
    return request({
        url: `${base_url}/deleteById/${id}`,
        method: 'delete'
    })
}

// 统计所有预约
export const CountAllBook = () => {
    return request({
        url: `${base_url}/countAllBook`,
        method: 'get'
    })
}