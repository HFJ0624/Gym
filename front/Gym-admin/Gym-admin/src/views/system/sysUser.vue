<template>

    <!---搜索表单-->
    <div class="search-div">
        <el-form label-width="70px" size="small">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="关键字">
                        <el-input v-model="queryDto.keyword"
                                style="width: 100%"
                                placeholder="用户名"
                                ></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="创建时间">
                        <el-date-picker v-model="createTimes"
                                        type="daterange"
                                        range-separator="To"
                                        start-placeholder="开始时间"
                                        end-placeholder="结束时间"
                                        format="YYYY-MM-DD"
                                        value-format="YYYY-MM-DD"
                                        />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row style="display:flex">
                <el-button type="primary" size="small" @click="searchSysUser">
                    搜索
                </el-button>
                <el-button size="small" @click="resetData">重置</el-button>
            </el-row>
        </el-form>
    </div>
    
    <div class="tools-div">
        <el-button type="success" size="small" @click="exportUserData">导出</el-button>
        <el-button type="primary" size="small" @click="importUserData">导入</el-button>
    </div>

    <el-dialog v-model="dialogImportVisible" title="导入" width="30%">
        <el-form label-width="120px">
            <el-form-item label="用户文件">
                <el-upload
                        class="upload-demo"
                        action="http://localhost:9601/admin/system/user/importData"
                        :on-success="onUploadSuccess"
                        :headers="headers"
                        >
                    <el-button type="primary">上传</el-button>
                </el-upload>
            </el-form-item>
        </el-form>
    </el-dialog>

    <!--添加按钮-->
    <div class="tools-div">
        <el-button type="success" size="small" @click="addShow">添 加</el-button>
    </div>

    <el-dialog v-model="dialogVisible" title="添加或修改" width="40%">
        <el-form label-width="120px">
            <el-form-item label="用户名">
                <el-input v-model="sysUser.username"/>
            </el-form-item>
            <el-form-item v-if="sysUser.id == null" label="密码">
                <el-input type="password" show-password v-model="sysUser.password"/>
            </el-form-item>
            <el-form-item label="真实姓名">
                <el-input v-model="sysUser.realName"/>
            </el-form-item>
            <el-form-item label="性别">
                <el-input v-model="sysUser.sex"/>
            </el-form-item>
            <el-form-item label="手机">
                <el-input v-model="sysUser.phone"/>
            </el-form-item>
            <el-form-item label="邮件">
                <el-input v-model="sysUser.email"/>
            </el-form-item>
            <el-form-item label="头像">
                <el-upload
                        class="avatar-uploader"
                        action="http://localhost:9601/admin/system/fileUpload"
                        :show-file-list="false"
                        :on-success="handleAvatarSuccess"
                        :headers="headers"
                        >
                    <img v-if="sysUser.avatar" :src="sysUser.avatar" class="avatar" />
                    <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                </el-upload>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="submit">提交</el-button>
                <el-button @click="dialogVisible = false">取消</el-button>
            </el-form-item>
        </el-form>
    </el-dialog> 

    <!---数据表格-->
    <el-table :data="list" style="width: 100%">
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="realName" label="姓名" />
        <el-table-column prop="sex" label="性别" />
        <el-table-column prop="phone" label="手机" />
        <el-table-column prop="email" label="邮件" />
        <el-table-column prop="avatar" label="头像" #default="scope">
            <img :src="scope.row.avatar" width="50" />
        </el-table-column>
        <el-table-column prop="role" label="角色" />

        <!-- 状态开关 -->
        <el-table-column label="状态" align="center" width="320">
            <template #default="scope">
                <!-- 核心：用计算属性适配数字/字符串，解决刷新匹配问题 -->
                <el-switch
                v-model="scope.row.status"
                :active-value="1"        
                :inactive-value="0"   
                active-color="#13ce66"
                inactive-color="#ff4949"
                @change="(val) => updateStatus(val, scope.row.id)"
                style="margin-right: 10px;"
                />
                <span>{{ scope.row.status == 1 ? '正常' : '停用' }}</span>
            </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" align="center" width="280" #default="scope">
            <el-button type="primary" size="small" @click="editSysUser(scope.row)">
                修改
            </el-button>
            <el-button type="danger" size="small" @click="deleteById(scope.row)">
                删除
            </el-button>
            <el-button type="warning" size="small" @click="showAssignRole(scope.row)">
                分配角色
            </el-button>
        </el-table-column>
    </el-table>

    <!-- 分配角色弹框 -->
    <el-dialog v-model="dialogRoleVisible" title="分配角色" width="40%">
        <el-form label-width="80px">
            <el-form-item label="用户名">
                <el-input disabled :value="sysUser.username"></el-input>
            </el-form-item>

            <el-form-item label="角色列表">
                <el-checkbox-group v-model="userRoleIds">
                    <el-checkbox v-for="role in allRoles" :key="role.id" :label="role.id">
                        {{ role.roleName }}
                    </el-checkbox>
                </el-checkbox-group>
            </el-form-item>

            <el-form-item>
                <el-button type="primary" @click="doAssign">提交</el-button>
                <el-button @click="dialogRoleVisible = false">取消</el-button>
            </el-form-item>
        </el-form>
    </el-dialog>

    <el-pagination
                v-model:current-page="pageParams.page"
                v-model:page-size="pageParams.limit"
                :page-sizes="[3, 5, 10, 20]"
                @size-change="fetchData"
                @current-change="fetchData"
                layout="total, sizes, prev, pager, next"
                :total="total"
                />

</template>

<script setup>
import { ref , onMounted } from 'vue'; 
import { useApp } from '@/pinia/modules/app';
import { GetUserListByPage,SaveUser,updateUserStatus,UpdateUser,DeleteUserById,DoAssignRoleToUser,ExportUserData } from '@/api/User';
import { ElMessage, ElMessageBox } from 'element-plus';
import { GetAllRoleList } from '@/api/Role'; 

//------------------------分页功能---------------------------------
// 表格数据模型
const list = ref([]);

// 分页条数据模型
const total = ref(0)

// 定义搜索表单数据模型
const queryDto = ref({
    keyword: "" ,
    createTimeBegin: "",
    createTimeEnd: ""
})
const createTimes = ref([])

//分页数据
const pageParamsForm = {
  page: 1, // 页码
  limit: 3, // 每页记录数
}
const pageParams = ref(pageParamsForm)

// onMounted钩子函数
onMounted(() => {
    fetchData();
})

// 搜素按钮点击事件处理函数
const searchSysUser = () => {
    fetchData()
}

// 重置按钮点击事件处理函数
const resetData = async () => {
    queryDto.value = {}
    createTimes.value = []
    pageParams.value.page = 1;
    await fetchData();
    ElMessage.success('重置成功');
}

// 定义分页查询方法
const fetchData = async () => {
    if (createTimes.value.length == 2) {
        queryDto.value.createTimeBegin = createTimes.value[0]
        queryDto.value.createTimeEnd = createTimes.value[1]
    }
    // 请求后端接口进行分页查询
    const { code , message , data } = await GetUserListByPage(pageParams.value.page , pageParams.value.limit , queryDto.value)
    list.value = data.list
    total.value = data.total
}

//------------------------添加用户功能---------------------------------
// 添加表单对话框显示隐藏控制变量
const dialogVisible = ref(false)
const addShow = () => {
    sysUser.value = {}
    dialogVisible.value = true 
}

// 修改按钮点击事件处理函数
const editSysUser = (row) => {
    dialogVisible.value = true 
    sysUser.value = {...row}
}

// 定义提交表单数据模型
const defaultForm = {
    username:"",
    realName: "" ,
    password: "",
    sex: "",
    phone: "" ,
    email: "" ,
    avatar: ""
}
const sysUser = ref(defaultForm)

// 提交按钮事件处理函数
const submit = async () => {
    if(!sysUser.value.id) {
        const {code , message , data} = await SaveUser(sysUser.value) 
        if(code === 200) {
            dialogVisible.value = false
            ElMessage.success('新增用户成功')
            fetchData()
        }else {
            ElMessage.error(message)
        }
    }else {
        const {code , message , data} = await UpdateUser(sysUser.value) 
        if(code === 200) {
            dialogVisible.value = false
            ElMessage.success('修改用户成功')
            fetchData()
        }else {
            ElMessage.error(message)
        }   
    }    
}

//------------------------修改用户状态功能---------------------------------
// 修改用户状态的核心方法
const updateStatus = async (newStatus, id) => {
  try {
    // 核心：newStatus现在是数字1/0，无需转换，直接传后端
    const { code, message } = await updateUserStatus({
      id: id,          
      status: newStatus // 直接传数字1/0，解决SQL报错
    });

    if (code === 200) {
      ElMessage.success(`状态已${newStatus === 1 ? '启用' : '禁用'}`);
      fetchData(); // 保留原有fetchData，不修改
    } else {
      // 回滚：用数字1/0匹配
      const targetRow = list.value.find(item => item.id === id);
      if (targetRow) {
        targetRow.status = newStatus === 1 ? 0 : 1;
      }
      ElMessage.error(message || '状态修改失败，请重试');
    }
  } catch (error) {
    const targetRow = list.value.find(item => item.id === id);
    if (targetRow) {
      targetRow.status = targetRow.status === 1 ? 0 : 1;
    }
    ElMessage.error('网络异常，修改失败');
    console.error('修改状态报错：', error);
  }
};

//------------------------删除用户功能---------------------------------
// 删除角色
const deleteById = (row) => {
    ElMessageBox.confirm('此操作将永久删除该记录, 是否继续?', 'Warning', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
       const {code } = await DeleteUserById(row.id)
       if(code === 200) {
            ElMessage.success('删除用户成功')
            fetchData()
       }
    })
}

//-----------------------上传头像功能---------------------------------
const headers = {
  token: useApp().authorization.token     // 从pinia中获取token，在进行文件上传的时候将token设置到请求头中
}

// 图像上传成功以后的事件处理函数
const handleAvatarSuccess = (response, uploadFile) => {
    sysUser.value.avatar = response.data
}

//-----------------------分配角色功能---------------------------------
// 角色列表
const userRoleIds = ref([])
const allRoles = ref([])
const dialogRoleVisible = ref(false)
const showAssignRole = async row => {
  sysUser.value = row
  dialogRoleVisible.value = true

  // 查询所有的角色数据
  const {code , message , data } = await GetAllRoleList(row.id) ;
  allRoles.value = data.allRolesList

  // 获取当前登录用户的角色数据
  userRoleIds.value = data.sysUserRoles

}

// 角色分配按钮事件处理函数
const doAssign = async () => {
    let assginRoleVo = {
        userId: sysUser.value.id ,
        roleIdList: userRoleIds.value
    }
    const { code , message , data} = await DoAssignRoleToUser(assginRoleVo) ;
    if(code === 200) {
        ElMessage.success('分配角色成功')
        dialogRoleVisible.value = false 
        fetchData()
    }
}

//------------------------导出用户数据功能---------------------------------
const exportUserData = () => {
  // 调用 ExportUserData() 方法获取导出数据
  ExportUserData().then(res => {
      // 创建 Blob 对象，用于包含二进制数据
      const blob = new Blob([res]);             
      // 创建 a 标签元素，并将 Blob 对象转换成 URL
      const link = document.createElement('a'); 
      link.href = window.URL.createObjectURL(blob);
      // 设置下载文件的名称
      link.download = '用户数据.xlsx';
      // 模拟点击下载链接
      link.click();
  })  
}

//------------------------导入用户数据功能---------------------------------
// 文件上传相关变量以及方法定义
const dialogImportVisible = ref(false)
const importUserData = () => {
  dialogImportVisible.value = true
}

// 上传文件成功以后要执行方法
const onUploadSuccess = async (response, file) => {
  ElMessage.success('操作成功')
  dialogImportVisible.value = false
  const { data } = await GetUserListByPage(pageParams.value.page , pageParams.value.limit , queryDto.value)
  list.value = data.list; 
  total.value = data.total;
}


</script>

<style scoped>
.search-div {
  margin-bottom: 10px;
  padding: 10px;
  border: 1px solid #ebeef5;
  border-radius: 3px;
  background-color: #fff;
}
.tools-div {
  margin: 10px 0;
  padding: 10px;
  border: 1px solid #ebeef5;
  border-radius: 3px;
  background-color: #fff;
}
.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}
</style>