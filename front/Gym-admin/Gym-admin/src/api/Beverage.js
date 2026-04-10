import request from '@/utils/request'

const base_url = '/admin/shopping/beverage'

// 分页查询
export const GetBeverageListByPage = (pageNum, pageSize, queryDto) => {
    return request({
        url: `${base_url}/findByPage/${pageNum}/${pageSize}`,
        method: 'post',
        data: queryDto,
    })
}

// 添加商品
export const SaveBeverage = (data) => {
    return request({
        url: `${base_url}/saveBeverage`,
        method: 'post',
        data
    })
}

// 修改商品
export const UpdateBeverage = (data) => {
    return request({
        url: `${base_url}/updateBeverage`,
        method: 'put',
        data
    })
}

// 删除商品
export const DeleteBeverageById = (beverageId) => {
    return request({
        url: `${base_url}/deleteById/${beverageId}`,
        method: 'delete'
    })
}