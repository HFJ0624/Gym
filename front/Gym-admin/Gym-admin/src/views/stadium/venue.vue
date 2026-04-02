<template>
    <div class="search-div">
        <!-- 搜索表单 - 重点放大优化 -->
        <el-form label-width="80px" size="default" class="search-form">
            <el-row :gutter="24">
                <el-col :span="8">
                    <el-form-item label="场馆名称" class="form-item-lg">
                        <el-input 
                            v-model="queryDto.venueName"
                            style="width: 100%"
                            placeholder="请输入场馆名称"
                            clearable
                            size="default"
                            class="input-lg"
                        ></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="场馆类型" class="form-item-lg">
                        <el-input 
                            v-model="queryDto.venueType"
                            style="width: 100%"
                            placeholder="请输入场馆类型"
                            clearable
                            size="default"
                            class="input-lg"
                        ></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="场馆电话" class="form-item-lg">
                        <el-input 
                            v-model="queryDto.phone"
                            style="width: 100%"
                            placeholder="请输入联系电话"
                            clearable
                            size="default"
                            class="input-lg"
                        ></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item>
                        <el-button type="primary" @click="searchVenue" icon="Search">
                            搜索
                        </el-button>
                        <el-button @click="resetData" icon="Refresh">重置</el-button>
                    </el-form-item>
                </el-col>
            </el-row>
        </el-form>

        <!-- 添加按钮 -->
        <div class="tools-div">
            <el-button type="success" @click="addShow" icon="Plus">添 加</el-button>
        </div>

        <!-- 添加角色表单对话框 -->
        <el-dialog v-model="dialogVisible" title="添加或修改场馆" width="560px" center class="venue-dialog">
            <el-form label-width="120px" class="dialog-form" :model="venue">
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="场馆名称" prop="venueName">
                            <el-input v-model="venue.venueName" placeholder="请输入场馆名称"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="场馆类型" prop="venueType">
                            <el-input  v-model="venue.venueType" placeholder="请输入场馆类型"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="场馆位置" prop="location">
                            <el-input  v-model="venue.location" placeholder="请输入场馆位置"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="场馆电话" prop="phone">
                            <el-input  v-model="venue.phone" placeholder="请输入场馆电话"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                    <el-form-item label="场馆容量" prop="capacity">
                        <el-input 
                            v-model="venue.capacity" 
                            placeholder="请输入场馆容量"
                            type="number"
                            oninput="value=value.replace(/[^0-9]/g,'')"
                        />
                    </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="描述" prop="description">
                            <el-input v-model="venue.description" placeholder="请输入描述" type="textarea" :rows="3"/>
                        </el-form-item>
                    </el-col>

                    <el-form-item label="头像">
                    <!-- 固定宽高的上传容器 -->
                    <el-upload
                        class="avatar-uploader"
                        action="http://localhost:9601/admin/system/fileUpload"
                        :show-file-list="false"
                        :on-success="handleAvatarSuccess"
                        :headers="headers"
                    >
                        <!-- 固定 80px 宽高，超出隐藏，图片居中裁剪 -->
                        <div style="width: 80px; height: 80px; overflow: hidden; border-radius: 4px; position: relative;">
                        <img 
                            v-if="venue.avatar" 
                            :src="venue.avatar" 
                            style="width: 100%; height: 100%; object-fit: cover; object-position: center;" 
                        />
                        <el-icon 
                            v-else 
                            style="font-size: 28px; color: #c0c4cc; position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%);"
                        >
                            <Plus />
                        </el-icon>
                        </div>
                    </el-upload>
                    </el-form-item>

                    <el-col :span="24">
                        <el-form-item label="营业时间" prop="timeRange">
                            <el-row :gutter="10">
                                <el-col :span="11">
                                <el-date-picker
                                    v-model="venue.openTime"
                                    type="datetime" 
                                    placeholder="开始营业时间"
                                    format="YYYY-MM-DD HH:mm:ss"
                                    value-format="YYYY-MM-DD HH:mm:ss"
                                    time-arrow-control
                                    style="width: 100%;"
                                    :picker-options="{ disabledDate: () => false }"
                                />
                                </el-col>
                                <el-col :span="2" style="text-align: center; line-height: 32px;">
                                To
                                </el-col>
                                <el-col :span="11">
                                <el-date-picker
                                    v-model="venue.closeTime"
                                    type="datetime"
                                    placeholder="结束营业时间"
                                    format="YYYY-MM-DD HH:mm:ss"
                                    value-format="YYYY-MM-DD HH:mm:ss"
                                    time-arrow-control
                                    style="width: 100%;"
                                    :picker-options="{ 
                                    disabledDate: (time) => {
                                        if (venue.openTime) {
                                        return time.getTime() < new Date(venue.openTime).getTime();
                                        }
                                        return false;
                                    }
                                    }"
                                />
                                </el-col>
                            </el-row>
                        </el-form-item>
                    </el-col>
                </el-row>
                
                <el-form-item>
                    <el-button type="primary" @click="submit">提交</el-button>
                    <el-button @click="dialogVisible = false">取消</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>
        
        <!--- 场馆表格数据 -->
        <el-table 
            :data="list" 
            style="width: 100%" 
            border 
            stripe 
            hover 
            class="venue-table"
            :empty-text="total === 0 ? '暂无场馆数据' : '加载中...'"
        >
            <el-table-column prop="venueName" label="场馆名称" min-width="150" />
            <el-table-column prop="venueType" label="场馆类型" min-width="150" />
            <el-table-column prop="location" label="场馆位置" min-width="180" />
            <el-table-column prop="phone" label="场馆电话" min-width="150" />
            <el-table-column prop="capacity" label="场馆容量" min-width="120" align="center" />

            <!-- 描述列 -->
            <el-table-column label="描述" min-width="180">
                <template #default="scope">
                    <div class="desc-wrapper">
                        <div class="desc-text">
                            {{ scope.row.description ? (scope.row.description.length > 10 ? scope.row.description.substring(0, 10) + '...' : scope.row.description) : '' }}
                        </div>
                        <el-button 
                            v-if="scope.row.description && scope.row.description.length > 10"
                            type="text" 
                            size="mini" 
                            class="desc-btn"
                            @click.stop="openDescDialog(scope.row.description)"
                        >
                            查看详情
                        </el-button>
                    </div>
                </template>
            </el-table-column>

            <el-table-column prop="avatar" label="场馆图片" #default="scope">
                <img :src="scope.row.avatar" width="50" />
            </el-table-column>
            <el-table-column prop="openTime" label="开始营业时间" min-width="180" />
            <el-table-column prop="closeTime" label="结束营业时间" min-width="180" />
            <el-table-column prop="createTime" label="创建时间" min-width="180" />
            <el-table-column label="操作" align="center" width="200">
                <template #default="scope">
                    <el-button type="primary" size="small" @click="editShow(scope.row)" icon="Edit">
                        修改
                    </el-button>
                    <el-button type="danger" size="small" @click="deleteById(scope.row)" icon="Delete">
                        删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>

        <!-- 描述详情对话框 -->
        <el-dialog
            title="场馆描述详情"
            v-model="descDialogVisible"
            width="600px"
            close-on-click-modal="true"
            destroy-on-close="true"
            class="desc-dialog">
            <div class="desc-dialog-content">
                {{ currentDescription }}
            </div>
            <template #footer>
                <el-button size="small" @click="descDialogVisible = false" icon="Close">关闭</el-button>
            </template>
        </el-dialog>

        <!--分页条-->
        <el-pagination
                v-model:current-page="pageParams.page"
                v-model:page-size="pageParams.limit"
                :page-sizes="[3, 5, 10, 20]"
                @size-change="fetchData"
                @current-change="fetchData"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"
                class="pagination-wrap"
                background
                />
  </div>
</template>

<script setup>
import { ref , onMounted } from 'vue'; 
import { useApp } from '@/pinia/modules/app';
import { GetVenueListByPage,SaveVenue,UpdateVenue,DeleteVenueById } from '@/api/Venue';
import { ElMessage, ElMessageBox } from 'element-plus';

//--------------------------------------分页查询功能--------------------------------------
let total = ref(0)
let list = ref([])

const pageParamsForm = {
    page: 1,
    limit: 3,
}

const pageParams = ref(pageParamsForm)
const queryDto = ref({
    venueName: "" ,
    venueType: "",
    phone: "",
})

onMounted(() => {
    fetchData();
})

const searchVenue = () => {
    fetchData();
}

const fetchData = async () => {
    const {data , code , message } = await GetVenueListByPage(pageParams.value.page , pageParams.value.limit , queryDto.value) ;
    list.value = data.list;
    total.value = data.total;
}

//--------------------------------------重置功能--------------------------------------
const resetData = async () => {
    queryDto.value = {}
    pageParams.value.page = 1
    await fetchData();
    ElMessage.success('重置成功');
}

//--------------------------------------展示详情信息功能--------------------------------------
const descDialogVisible = ref(false)
const currentDescription = ref('')

const openDescDialog = (description) => {
    currentDescription.value = description || '暂无描述信息';
    descDialogVisible.value = true;
}

//-------------------------------------添加或修改功能--------------------------------------
const dialogVisible = ref(false)

const addShow = () => {
    venue.value = {}
  	dialogVisible.value = true
}

const editShow = (row) => {
    venue.value = {...row}
    dialogVisible.value = true
}

const defaultForm = {
    id: "",
    venueName: "",
    venueType: "",
    location: "",
    phone: "",
    capacity: "",
    description: "",
    openTime: "",
    closeTime: "",
    avatar: "",
}
const venue = ref(defaultForm)

const submit = async () => {
    if(!venue.value.id) {
        const {code , message , data} = await SaveVenue(venue.value) 
        if(code === 200) {
            dialogVisible.value = false
            ElMessage.success('新增场馆成功')
            fetchData()
        }else {
            ElMessage.error(message)
        }
    }else {
        const {code , message , data} = await UpdateVenue(venue.value) 
        if(code === 200) {
            dialogVisible.value = false
            ElMessage.success('修改场馆成功')
            fetchData()
        }else {
            ElMessage.error(message)
        }   
    }    
}

//-------------------------------------删除场馆功能--------------------------------------
const deleteById = (row) => {
    ElMessageBox.confirm('此操作将永久删除该记录, 是否继续?', 'Warning', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
       const {code } = await DeleteVenueById(row.id)
       if(code === 200) {
            ElMessage.success('删除场馆成功')
            pageParams.value.page = 1
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
    venue.value.avatar = response.data
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

/* 描述对话框内容样式（核心：自动换行+滚动） */
.desc-dialog-content {
  padding: 10px 0;
  line-height: 1.8;          /* 行高，提升阅读体验 */
  white-space: pre-wrap;     /* 保留换行符，支持手动换行 */
  word-wrap: break-word;     /* 中文自动换行 */
  word-break: break-all;     /* 英文/数字强制换行 */
  max-height: 400px;         /* 最大高度，避免弹窗过高 */
  overflow-y: auto;          /* 超出高度显示滚动条 */
  color: #333;               /* 文字颜色，更易阅读 */
}
.avatar-uploader .el-upload {
  width: 100px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader .avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.avatar-uploader .avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
</style>