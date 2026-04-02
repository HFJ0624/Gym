import request from '@/utils/request'

const base_url = '/admin/stadium/equipment'

// 分页查询
export const GetEquipmentListByPage = (pageNum, pageSize, queryDto) => {
    return request({
        url: `${base_url}/findByPage/${pageNum}/${pageSize}`,
        method: 'post',
        data: queryDto,
    })
}

// 添加场地请求方法
export const saveEquipment = (data) => {
    return request({
        url: `${base_url}/saveEquipment`,
        method: 'post',
        data
    })
}

// 修改场地
export const updateEquipment = (data) => {
    return request({
        url: `${base_url}/updateEquipment`,
        method: 'put',
        data
    })
}

// 删除场地
export const DeleteEquipmentById = (equipmentId) => {
    return request({
        url: `${base_url}/deleteById/${equipmentId}`,
        method: 'delete'
    })
}