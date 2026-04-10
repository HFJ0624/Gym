import request from '@/utils/request'

const base_url = '/admin/shopping/outfit'

// 分页查询
export const GetOutfitListByPage = (pageNum, pageSize, queryDto) => {
    return request({
        url: `${base_url}/findByPage/${pageNum}/${pageSize}`,
        method: 'post',
        data: queryDto,
    })
}

// 添加器材
export const SaveOutfit = (data) => {
    return request({
        url: `${base_url}/saveOutfit`,
        method: 'post',
        data
    })
}

// 修改器材
export const UpdateOutfit = (data) => {
    return request({
        url: `${base_url}/updateOutfit`,
        method: 'put',
        data
    })
}

// 删除器材
export const DeleteOutfitById = (outfitId) => {
    return request({
        url: `${base_url}/deleteById/${outfitId}`,    
        method: 'delete'
    })
}