<template>
    <div class="search-div">
        <!-- 搜索表单 -->
        <el-form label-width="70px" size="small">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="标题" label-width="10">
                        <el-input v-model="queryDto.title"
                                style="width: 100%"
                                placeholder="请输入查询标题"
                                ></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="内容">
                        <el-input v-model="queryDto.content"
                                style="width: 100%"
                                placeholder="请输入查询内容"
                                ></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="通知类型">
                        <el-input v-model="queryDto.noticeType"
                                style="width: 100%"
                                placeholder="请输入查询通知类型"
                                ></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row style="display:flex">
                <el-button type="primary" size="small" @click="searchNotice">
                    搜索
                </el-button>
                <el-button size="small" @click="resetData">重置</el-button>
            </el-row>
        </el-form>

        <!-- 添加按钮 -->
        <div class="tools-div">
            <el-button type="success" size="small" @click="addShow">添 加</el-button>
        </div>

        <!-- 添加通知表单对话框 -->
        <el-dialog v-model="dialogVisible" title="添加或修改通知" width="460px" center>
            <el-form label-width="120px">
                <el-form-item label="标题">
                    <el-input  v-model="notice.title" placeholder="请输入标题"/>
                </el-form-item>

                <el-form-item label="内容">
                    <el-input  
                    type="textarea"
                    :rows="4"
                    v-model="notice.content" placeholder="请输入内容"/>
                </el-form-item>

                <el-form-item label="通知类型">
                    <el-select v-model="notice.noticeType" placeholder="请选择通知类型">
                        <el-option label="系统公告" value="1" />
                        <el-option label="活动通知" value="2" />
                        <el-option label="新闻" value="3" />
                    </el-select>
                </el-form-item>

                <el-form-item label="状态">
                    <el-select v-model="notice.status" placeholder="请选择状态">
                        <el-option label="草稿" value="0" />
                        <el-option label="已发布" value="1" />
                        <el-option label="已下架" value="2" />
                    </el-select>
                </el-form-item>
                
                <el-form-item class="dialog-btn-wrap">
                    <el-button type="primary" @click="submit">提交</el-button>
                    <el-button @click="dialogVisible = false">取消</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>
        
        <!--- 场馆表格数据 -->
        <el-table :data="list" style="width: 100%">
            <el-table-column prop="username" label="创建人名称" width="180" />
            <el-table-column prop="avatar" label="创建人头像" #default="scope">
                <img :src="scope.row.avatar" width="50" />
            </el-table-column>
            <el-table-column prop="title" label="公告标题" width="180" />

            <!-- 描述列 -->
            <el-table-column label="公告内容" min-width="180">
                <template #default="scope">
                    <div class="desc-wrapper">
                        <div class="desc-text">
                            {{ scope.row.content ? (scope.row.content.length > 10 ? scope.row.content.substring(0, 10) + '...' : scope.row.content) : '' }}
                        </div>
                        <el-button 
                            v-if="scope.row.content && scope.row.content.length > 10"
                            type="text" 
                            size="mini" 
                            class="desc-btn"
                            @click.stop="openDescDialog(scope.row.content)"
                        >
                            查看详情
                        </el-button>
                    </div>
                </template>
            </el-table-column>

            <el-table-column prop="nums" label="评论数量" width="180" />
            <el-table-column prop="noticeType" label="公告类型" width="180">
              <template #default="scope">
                <span v-if="scope.row.noticeType === 1">系统公告</span>
                <span v-else-if="scope.row.noticeType === 2">活动通知</span>
                <span v-else-if="scope.row.noticeType === 3">新闻</span>
                <span v-else>{{ scope.row.noticeType }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="180">
              <template #default="scope">
                <span v-if="scope.row.status === 0">草稿</span>
                <span v-else-if="scope.row.status === 1">已发布</span>
                <span v-else-if="scope.row.status === 2">已下架</span>
                <span v-else>{{ scope.row.status }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="300" />
            <el-table-column label="操作" align="center" width="280" #default="scope">
            <el-button type="primary" size="small" @click="editShow(scope.row)">
                修改
            </el-button>
            <el-button type="danger" size="small" @click="deleteById(scope.row)">
                删除
            </el-button>
            </el-table-column>
        </el-table>

        <!-- 描述详情对话框 -->
        <el-dialog
            title="公告具体内容"
            v-model="descDialogVisible"
            width="600px"
            close-on-click-modal="true"
            destroy-on-close="true"
            class="desc-dialog">
            <div class="desc-dialog-content">
                {{ currentContent }}    
            </div>
            <template #footer>
                <el-button size="small" @click="descDialogVisible = false" icon="el-icon-close" class="dialog-footer-btn">关闭</el-button>
            </template>
        </el-dialog>

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
import { GetNoticeListByPage,SaveNotice,UpdateNotice,DeleteNoticeById } from '@/api/Notice';
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
    title: "",
    content: "" ,
    noticeType: "",   
})

// onMounted钩子函数
onMounted(async () => {
    await getUserInfo();
    fetchData();
})

// 搜索按钮点击事件处理函数
const searchNotice = () => {
    fetchData();
}

// 远程调用后端分页查询接口
const fetchData = async () => {
    const {data , code , message } = await GetNoticeListByPage(pageParams.value.page , pageParams.value.limit , queryDto.value) ;
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

//--------------------------------------展示详情信息功能--------------------------------------
const descDialogVisible = ref(false)
const currentContent = ref('')

const openDescDialog = (content) => {
    currentContent.value = content || '暂无描述信息';
    descDialogVisible.value = true;
}

//-------------------------------------添加或修改功能--------------------------------------
// 控制对话是否展示的变量
const dialogVisible = ref(false)

// 修改按钮点击事件处理函数
const editShow = (row) => {
    notice.value = {...row}
    dialogVisible.value = true
}

//表单数据模型
const defaultForm = {
    id: "",
    createId: null,
    title: "",
    content: "",
    noticeType: "",
    status: "",
}
const notice = ref(defaultForm)

//进入添加
const addShow = () => {
    notice.value = {
        createId: userId.value,
        title: "",
        content: "",
        noticeType: "",
        status: "",
    };
    dialogVisible.value = true;
};


// 提交按钮事件处理函数
const submit = async () => {
    if(!notice.value.id) {
        const {code , message , data} = await SaveNotice(notice.value) 
        if(code === 200) {
            dialogVisible.value = false
            ElMessage.success('新增公告成功')
            fetchData()
        }else {
            ElMessage.error(message)
        }
    }else {
        const {code , message , data} = await UpdateNotice(notice.value) 
        if(code === 200) {
            dialogVisible.value = false
            ElMessage.success('修改公告成功')
            fetchData()
        }else {
            ElMessage.error(message)
        }   
    }    
}

//-------------------------------------删除器械功能--------------------------------------
const deleteById = (row) => {
    ElMessageBox.confirm('此操作将永久删除该记录, 是否继续?', 'Warning', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
       const {code } = await DeleteNoticeById(row.id)
       if(code === 200) {
            ElMessage.success('删除公告成功')
            pageParams.value.page = 1
            fetchData()
       }
    })
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
