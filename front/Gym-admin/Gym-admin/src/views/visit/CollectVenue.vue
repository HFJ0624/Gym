<template>
    <div class="search-div">
        <!-- 搜索表单 -->
        <el-form label-width="70px" size="small">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="用户名" label-width="10">
                        <el-input v-model="queryDto.username"
                                style="width: 100%"
                                placeholder="请输入查询用户名"
                                ></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="场馆名称" label-width="10">
                        <el-input v-model="queryDto.venueName"
                                style="width: 100%"
                                placeholder="请输入查询场馆名称"
                                ></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row style="display:flex">
                <el-button type="primary" size="small" @click="searchCollect">
                    搜索
                </el-button>
                <el-button size="small" @click="resetData">重置</el-button>
            </el-row>
        </el-form>
        
        <!--- 场馆表格数据 -->
        <el-table :data="list" style="width: 100%">
            <el-table-column prop="username" label="收藏人名称" width="180" />
            <el-table-column prop="avatar" label="收藏人头像" #default="scope">
                <img :src="scope.row.avatar" width="50" />
            </el-table-column>
            <el-table-column prop="venueName" label="收藏场馆名称" width="180" />
            <el-table-column prop="collect" label="是否收藏" width="180">
              <template #default="scope">
                <span v-if="scope.row.collect === 1">是</span>
                <span v-else-if="scope.row.collect === 0">否</span>
                <span v-else>{{ scope.row.collect }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="300" />
        </el-table>

        <!--分页条-->
        <el-pagination
                v-model:current-page="pageParams.page"
                v-model:page-size="pageParams.limit"
                :page-sizes="[3, 5, 10, 20]"
                @size-change="fetchData"
                @current-change="fetchData"
                layout="total, sizes, prev, pager, next"
                :total="total"
                />
  </div>

</template>

<script setup>
import { ref , onMounted } from 'vue'; 
import { useApp } from '@/pinia/modules/app';
import { useAccount } from '@/pinia/modules/account';
import { GetVenueCollectListByPage } from '@/api/VenueCollect';
import { ElMessage, ElMessageBox } from 'element-plus';

// 获取当前登录用户信息
const accountStore = useAccount()
let userId = ref(null)

// 获取用户信息
const getUserInfo = async () => {
  try {
    await accountStore.getUserinfo()
    userId.value = accountStore.userinfo?.id
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

//--------------------------------------分页查询功能--------------------------------------
// 分页条总记录数
let total = ref(0)

// 定义表格数据模型
let list = ref([])

const pageParamsForm = {
    page: 1, //页码
    limit: 3, //每页记录数
}

const pageParams = ref(pageParamsForm)

// 搜索表单数据
const queryDto = ref({
    username: "",
    venueName: "" ,   
})

// onMounted钩子函数
onMounted(async () => {
    await getUserInfo();
    fetchData();
})

// 搜索按钮点击事件处理函数
const searchCollect = () => {
    fetchData();
}

// 远程调用后端分页查询接口
const fetchData = async () => {
    const {data , code , message } = await GetVenueCollectListByPage(pageParams.value.page , pageParams.value.limit , queryDto.value) ;
    list.value = data.list;
    total.value = data.total;
}

//--------------------------------------重置功能--------------------------------------
// 重置按钮点击事件处理函数
const resetData = async () => {
    queryDto.value = {}
    pageParams.value.page = 1
    await fetchData();
    ElMessage.success('重置成功');
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
