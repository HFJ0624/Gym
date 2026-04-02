<template>
    <div class="search-div">
        <!-- 搜索表单 -->
        <el-form label-width="70px" size="small">
            <el-form-item label="角色名称">
                <el-input
                        v-model="queryDto.roleName"
                        style="width: 100%"
                        placeholder="角色名称"
                        clearable
                        ></el-input>
            </el-form-item>
            <el-form-item label="角色编码">
                <el-input
                        v-model="queryDto.roleCode"
                        style="width: 100%"
                        placeholder="角色编码"
                        clearable
                        ></el-input>
            </el-form-item>
            <el-row class="btn-row">
                <el-button type="primary" size="small" @click="searchSysRole">
                    搜索
                </el-button>
                <el-button size="small" @click="resetData">重置</el-button>
            </el-row>
        </el-form>

        <!-- 添加按钮 -->
        <div class="tools-div">
            <el-button type="success" size="small" @click="addShow">添 加</el-button>
        </div>

        <!-- 添加角色表单对话框 -->
        <el-dialog v-model="dialogVisible" title="添加或修改角色" width="460px" center>
            <el-form label-width="120px">
                <el-form-item label="角色名称">
                    <el-input v-model="sysRole.roleName" placeholder="请输入角色名称"/>
                </el-form-item>
                <el-form-item label="角色Code">
                    <el-input  v-model="sysRole.roleCode" placeholder="请输入角色编码"/>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input v-model="sysRole.description" placeholder="请输入描述" type="textarea" :rows="3"/>
                </el-form-item>
                <el-form-item class="dialog-btn-wrap">
                    <el-button type="primary" @click="submit">提交</el-button>
                    <el-button @click="dialogVisible = false">取消</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>
        
        <!--- 角色表格数据 -->
        <el-table 
            :data="list" 
            style="width: 100%"
            border
            stripe
            highlight-current-row
            :header-cell-style="{background: '#f9fafb', color: '#333', fontWeight: 500}"
        >
            <el-table-column prop="roleName" label="角色名称" width="180" />
            <el-table-column prop="roleCode" label="角色code" width="180" />
            <el-table-column prop="description" label="描述" width="180" />
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column label="操作" align="center" width="280">
                <template #default="scope">
                    <el-button type="primary" size="small" @click="editShow(scope.row)">
                        修改
                    </el-button>
                    <el-button type="danger" size="small" @click="deleteById(scope.row)">
                        删除
                    </el-button>
                    <el-button type="warning" size="small" @click="showAssignMenu(scope.row)">
                        分配菜单
                    </el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 分配菜单的对话框 tree组件添加ref属性，后期方便进行tree组件对象的获取-->
        <el-dialog v-model="dialogMenuVisible" title="分配菜单" width="40%">
            <el-form label-width="80px">
                <el-tree
                        :data="sysMenuTreeList"
                        ref="tree"   
                        show-checkbox
                        default-expand-all
                        :check-on-click-node="true"
                        node-key="id"
                        :props="defaultProps"
                />
                <el-form-item>
                    <el-button type="primary" @click="doAssign">提交</el-button>
                    <el-button @click="dialogMenuVisible = false">取消</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>

        <!--分页条-->
        <div class="pagination-wrap">
            <el-pagination
                v-model:current-page="pageParams.page"
                v-model:page-size="pageParams.limit"
                :page-sizes="[3, 5, 10, 20]"
                @size-change="fetchData"
                @current-change="fetchData"
                layout="total, sizes, prev, pager, next"
                :total="total"
                background
            />
        </div>
  </div>
</template>

<script setup>
import { ref , onMounted } from 'vue';
import { GetSysRoleListByPage,SaveRole,UpdateRole,DeleteRoleById,GetSysRoleMenuIds,DoAssignMenuIdToSysRole } from '@/api/Role';
import { ElMessage,ElMessageBox } from 'element-plus';
//---------------------------------------分页条件查询--------------------------------------
// 分页条总记录数
let total = ref(0)

// 定义表格数据模型
let list = ref([])

//分页数据
const pageParamsForm = {
  page: 1, // 页码
  limit: 3, // 每页记录数
}
const pageParams = ref(pageParamsForm)     // 将pageParamsForm包装成支持响应式的对象

// 搜索表单数据
const queryDto = ref({
    "roleName": "",
    "roleCode": "",
})

// 页面加载完毕以后请求后端接口获取数据
onMounted(() => {
    fetchData() ;
})

// 搜索按钮点击事件处理函数
const searchSysRole = () => {
    fetchData() ;
}

// 远程调用后端分页查询接口
const fetchData = async () => {
    const {data , code , message } = await GetSysRoleListByPage(pageParams.value.page , pageParams.value.limit , queryDto.value) ;
    list.value = data.list;
    total.value = data.total;
}

// 重置按钮点击事件处理函数
const resetData = async () => {
  try {
    // 1. 清空搜索框内容
    queryDto.value.roleName = "";
    queryDto.value.roleCode = "";
    // 2. 重置分页到第一页
    pageParams.value.page = 1;
    // 3. 重新请求数据（等待数据加载完成）
    await fetchData();
    // 4. 提示重置成功
    ElMessage.success('重置成功');
  } catch (error) {
    // 异常处理，提示用户
    ElMessage.error('重置失败，请稍后重试');
  }
};

//---------------------------------------添加功能--------------------------------------
// 控制对话是否展示的变量
const dialogVisible = ref(false)

//进入添加
const addShow = () => {
    sysRole.value = {}
  	dialogVisible.value = true
}

//表单数据模型
const defaultForm = {
    id: "",
    roleCode: "",
    roleName: "",
    description: "",
}
const sysRole = ref(defaultForm)   // 使用ref包裹该对象，使用reactive不方便进行重置
 
// 添加角色或修改
const submit = async () => {
    if(!sysRole.value.id) {
        const { code } = await SaveRole(sysRole.value) ;
        if(code === 200) {
            dialogVisible.value = false
            ElMessage.success('操作成功')
            fetchData()
        }
    }else {
        const { code } = await UpdateRole(sysRole.value) ;
        if(code === 200) {
            dialogVisible.value = false
            ElMessage.success('修改成功')
            fetchData()
        }
    }
}

// 修改按钮点击事件处理函数
const editShow = (row) => {
    sysRole.value = {...row}
    dialogVisible.value = true
}

//---------------------------------------删除功能--------------------------------------
//删除数据
const deleteById = (row) => {
    ElMessageBox.confirm('此操作将永久删除该记录, 是否继续?', 'Warning', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
       const {code } = await DeleteRoleById(row.id)
       if(code === 200) {
            ElMessage.success('删除成功')
            pageParams.value.page = 1
            fetchData()
       }
    })
}

//--------------------------------------回显角色列表功能--------------------------------------
const defaultProps = {
  children: 'children',
  label: 'title',
}
const dialogMenuVisible = ref(false)
const sysMenuTreeList = ref([])

// 树对象变量
const tree = ref() 

// 默认选中的菜单数据集合
let roleId = ref()
const showAssignMenu = async row => { 
  dialogMenuVisible.value = true
  roleId = row.id
  const { data } = await GetSysRoleMenuIds(row.id)   // 请求后端地址获取所有的菜单数据，以及当前角色所对应的菜单数据
  sysMenuTreeList.value = data.sysMenuList
  tree.value.setCheckedKeys(data.roleMenuIds)   // 进行数据回显
}

//--------------------------------------分配角色功能--------------------------------------
const doAssign = async () => {
    const checkedNodes = tree.value.getCheckedNodes() ; // 获取选中的节点
    const checkedNodesIds = checkedNodes.map(node => {  // 获取选中的节点的id
        return {
            id: node.id,
            isHalf: 0
        }
    })
        
    // 获取半选中的节点数据，当一个节点的子节点被部分选中时，该节点会呈现出半选中的状态
    const halfCheckedNodes = tree.value.getHalfCheckedNodes() ; 
    const halfCheckedNodesIds = halfCheckedNodes.map(node => {   // 获取半选中节点的id
        return {
            id: node.id,
            isHalf: 1
        }
    })
        
    // 将选中的节点id和半选中的节点的id进行合并
    const menuIds = [...checkedNodesIds , ...halfCheckedNodesIds]  
    console.log(menuIds);

    // 构建请求数据
    const assignMenuDto = {
        roleId: roleId,
        menuIdList: menuIds
    }
 
    // 发送请求
    await DoAssignMenuIdToSysRole(assignMenuDto) ;
    ElMessage.success('分配角色成功')
    dialogMenuVisible.value = false

} 

</script>

<style scoped>
/* 全局基础样式优化 */
.search-div {
  margin: 0 16px 16px;
  padding: 20px;
  border: none;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

/* 搜索按钮行样式 */
.btn-row {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

/* 工具按钮区域优化 */
.tools-div {
  margin: 16px 0;
  padding: 16px 20px;
  border: none;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

/* 分页容器样式 */
.pagination-wrap {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 对话框按钮居中 */
.dialog-btn-wrap {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 8px;
}
/* 所有按钮文字垂直+水平居中（核心修复） */
:deep(.el-button) {
  display: inline-flex !important;
  align-items: center !important;
  justify-content: center !important;
  padding: 8px 16px !important;
}

/* 表格样式优化 */
:deep(.el-table) {
  --el-table-header-text-color: #333;
  --el-table-row-hover-bg-color: #f8f9fa;
}

:deep(.el-table__header th) {
  border-bottom: 1px solid #ebeef5 !important;
}

:deep(.el-table__body td) {
  border-bottom: 1px solid #f2f2f2 !important;
}

/* 输入框样式优化 - 恢复黑色边框 */
:deep(.el-input__wrapper) {
  border-radius: 4px;
  box-shadow: none;
  border: 1px solid #000 !important; /* 黑色边框 */
}

/* 文本域同样显示黑色边框 */
:deep(.el-textarea__wrapper) {
  border-radius: 4px;
  border: 1px solid #000 !important; /* 黑色边框 */
}

/* 输入框聚焦时边框样式（可选优化，保持黑色系） */
:deep(.el-input__wrapper:focus-within) {
  border-color: #000 !important;
  box-shadow: 0 0 0 1px rgba(0, 0, 0, 0.2) !important;
}

</style>