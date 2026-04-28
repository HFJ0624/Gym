import request from '@/utils/request'

const base_url = '/admin/booking/signIn'

// 分页查询
export const GetSignInListByPage = (pageNum, pageSize, queryDto) => {
    return request({
        url: `${base_url}/findByPage/${pageNum}/${pageSize}`,
        method: 'post',
        data: queryDto,
    })
}