<template>
    <div class="search-div">
        <!-- 搜索表单 -->
        <el-form label-width="70px" size="small">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="评论内容" label-width="10">
                        <el-input v-model="queryDto.content"
                                style="width: 100%"
                                placeholder="请输入查询评论内容"
                                ></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="评论人">
                        <el-input v-model="queryDto.username"
                                style="width: 100%"
                                placeholder="请输入查询评论人"
                                ></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row style="display:flex">
                <el-button type="primary" size="small" @click="searchNoticeComment">
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
        <el-dialog v-model="dialogVisible" title="添加或修改公告评论" width="460px" center>
            <el-form label-width="120px">

                <!-- 公告标题选择器 -->
                <el-form-item label="公告标题">
                    <el-select v-model="noticeComment.noticeId" placeholder="请选择公告标题">
                        <el-option v-for="item in noticeList" :key="item.id" :label="item.title" :value="item.id" />
                    </el-select>
                </el-form-item>

                <el-form-item label="内容">
                    <el-input  v-model="noticeComment.content" placeholder="请评论内容"/>
                </el-form-item>

                <el-form-item label="评价">
                    <el-select v-model="noticeComment.esteem" placeholder="请选择评论评价">
                        <el-option label="好评" value="5" />
                        <el-option label="良好" value="4" />
                        <el-option label="中等" value="3" />
                        <el-option label="一般" value="2" />
                        <el-option label="差评" value="1" />
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
            <el-table-column prop="username" label="评论人" width="180" />
            <el-table-column prop="avatar" label="评论人头像" #default="scope">
                <img :src="scope.row.avatar" width="50" />
            </el-table-column>
            <el-table-column prop="title" label="公告标题" width="180" />

            <!-- 描述列 -->
            <el-table-column label="评论内容" min-width="180">
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

            <el-table-column prop="esteem" label="评价" width="180">
              <template #default="scope">
                <span v-if="scope.row.esteem === 1">差评</span>
                <span v-else-if="scope.row.esteem === 2">一般</span>
                <span v-else-if="scope.row.esteem === 3">中等</span>
                <span v-else-if="scope.row.esteem === 4">良好</span>
                <span v-else-if="scope.row.esteem === 5">好评</span>
                <span v-else>{{ scope.row.esteem }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="180">
              <template #default="scope">
                <span v-if="scope.row.status === 1">通过</span>
                <span v-else-if="scope.row.status === 2">屏蔽</span>
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
import { GetNoticeCommentListByPage,SaveNoticeComment,UpdateNoticeComment,DeleteNoticeCommentById } from '@/api/NoticeComment';
import { ElMessage, ElMessageBox } from 'element-plus';
import { FindAllNotice } from '@/api/Notice';


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

let noticeList = ref([])

const pageParamsForm = {
    page: 1, //页码
    limit: 3, //每页记录数
}

const pageParams = ref(pageParamsForm)

// 搜索表单数据
const queryDto = ref({
    content: "" ,
    username: "" 
})

// onMounted钩子函数
onMounted(async () => {
    await getUserInfo();
    fetchData();
    loadNoticeList();
})

// 搜索按钮点击事件处理函数
const searchNoticeComment = () => {
    fetchData();
}

// 远程调用后端分页查询接口
const fetchData = async () => {
    const {data , code , message } = await GetNoticeCommentListByPage(pageParams.value.page , pageParams.value.limit , queryDto.value) ;
    list.value = data.list;
    total.value = data.total;
}

//封装公告列表加载函数（单独抽离，避免冗余）
const loadNoticeList = async () => {
    const response = await FindAllNotice();
    const data = response?.data || {};
    noticeList.value = data.allNotice;
};

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
    // 先从 row 中获取 noticeId，如果没有则从 noticeList 中根据 title 匹配
    let targetNoticeId = row.noticeId
    if (!targetNoticeId && row.title) {
        const matchedNotice = noticeList.value.find(notice => notice.title === row.title)
        if (matchedNotice) {
            targetNoticeId = matchedNotice.id
        }
    }
    
    noticeComment.value = {
        id: row.id,
        noticeId: targetNoticeId,
        userId: row.userId,
        content: row.content,
        status: row.status,
        esteem: row.esteem,
    }
    dialogVisible.value = true
}

//表单数据模型
const defaultForm = {
    id: "",
    noticeId: "",
    userId: "",
    content: "",
    status: "",
    esteem: "",
}
const noticeComment = ref(defaultForm)

//进入添加
const addShow = () => {
    noticeComment.value = {
        noticeId: "",
        userId: userId.value, 
        content: "",
        status: "",
        esteem: "",
    };
    dialogVisible.value = true;
};

// 提交按钮事件处理函数
const submit = async () => {
    if(!noticeComment.value.id) {
        const {code , message , data} = await SaveNoticeComment(noticeComment.value) 
        if(code === 200) {
            dialogVisible.value = false
            ElMessage.success('新增公告评论成功')
            fetchData()
        }else {
            ElMessage.error(message)
        }
    }else {
        const {code , message , data} = await UpdateNoticeComment(noticeComment.value) 
        if(code === 200) {
            dialogVisible.value = false
            ElMessage.success('修改公告评论成功')
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
       const {code } = await DeleteNoticeCommentById(row.id)
       if(code === 200) {
            ElMessage.success('删除公告评论成功')
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