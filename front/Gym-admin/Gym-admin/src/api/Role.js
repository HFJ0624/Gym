import request from '@/utils/request'

const base_url = '/admin/system/role'

// 分页查询角色数据
export const GetSysRoleListByPage = (pageNum, pageSize, queryDto) => {
    return request({
        url: `${base_url}/findByPage/${pageNum}/${pageSize}`,
        method: 'post',
        //接口有@RequestBody注解,前端 data: 名称,以json格式传输
        //接口没有@RequestBody注解,前端 params: 名称
        data: queryDto,
    })
}

// 添加角色请求方法
export const SaveRole = (data) => {
    return request({
        url: `${base_url}/saveSysRole`,
        method: 'post',
        data
    })
}

// 保存修改
export const UpdateRole = (data) => {
    return request({
        url: `${base_url}/updateSysRole`,
        method: 'put',
        data
    })
}

// 删除角色
export const DeleteRoleById = (roleId) => {
    return request({
        url: `${base_url}/deleteById/${roleId}`,
        method: 'delete'
    })
}

// 查询所有的角色数据
export const GetAllRoleList = (userId) => {
    return request({
        url: `${base_url}/findAllRoles/${userId}`,
        method: 'get'
    })
}

// 查询指定角色所对应的菜单id
export const GetSysRoleMenuIds = (roleId) => {
    return request({
        url: "/admin/system/roleMenu/findRoleMenuByRoleId/" + roleId,
        method: 'get'
    })
}

// 根据角色分配菜单请求方法
export const DoAssignMenuIdToSysRole = (assignMenuDto) => {
    return request({
        url: "/admin/system/roleMenu/doAssign",
        method: 'post',
        data: assignMenuDto
    })
}