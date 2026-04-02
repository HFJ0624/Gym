import request from '@/utils/request'

const base_url = '/admin/system/operaLog'

// 分页查询
export const GetLogListByPage = (pageNum, pageSize, queryDto) => {
    return request({
        url: `${base_url}/findByPage/${pageNum}/${pageSize}`,
        method: 'post',
        data: queryDto,
    })
}

// 根据id删除日志
export const DeleteOperaLogById = (id) => {
    return request({
        url: `${base_url}/deleteById/${id}`,
        method: 'delete'
    })
}