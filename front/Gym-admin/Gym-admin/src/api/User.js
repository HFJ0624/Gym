import request from '@/utils/request'

const base_url = '/admin/system/user'

// 分页查询
export const GetUserListByPage = (pageNum, pageSize, queryDto) => {
    return request({
        url: `${base_url}/findByPage/${pageNum}/${pageSize}`,
        method: 'post',
        data: queryDto,
    })
}

// 新增用户的方法
export const SaveUser = (data) => {
    return request({
        url: `${base_url}/saveUser`,
        method: "post",
        data
    })
}

// 新增用户的方法
export const Register = (data) => {
    return request({
        url: `${base_url}/register`,
        method: "post",
        data
    })
}

// 修改用户状态方法
export const updateUserStatus = (data) => {
    return request({
        url: `${base_url}/updateUserStatus`,
        method: "put",
        data
    })
}

// 修改用户数据的方法
export const UpdateUser = (data) => {
    return request({
        url: `${base_url}/updateUser`,
        method: "put",
        data
    })
}

// 根据id删除用户
export const DeleteUserById = (userId) => {
    return request({
        url: `${base_url}/deleteById/${userId}`,
        method: 'delete'
    })
}

// 给用户分配角色请求
export const DoAssignRoleToUser = (assginRoleVo) => {
    return request({
        url: `${base_url}/doAssign`,
        method: 'post',
        data: assginRoleVo
    })
}

// 统计用户数量
export const findGender = () => {
    return request({
        url: `${base_url}/findGender`,
        method: 'post'
    })
}

// 导出方法
export const ExportUserData = () => {
    return request({
        url: `${base_url}/exportData`,
        method: 'get',
        responseType: 'blob'  // // 这里指定响应类型为blob类型,二进制数据类型，用于表示大量的二进制数据
    })
}

