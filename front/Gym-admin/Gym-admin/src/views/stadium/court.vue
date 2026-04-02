<template>
    <div class="search-div">
        <!-- 搜索表单 -->
        <el-form label-width="70px" size="small">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="场地名称">
                        <el-input v-model="queryDto.name"
                                style="width: 100%"
                                placeholder="请输入场地名称"
                                ></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="场地类型">
                        <el-input v-model="queryDto.type"
                                style="width: 100%"
                                placeholder="请输入场地类型"
                                ></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row style="display:flex">
                <el-button type="primary" size="small" @click="searchVenue">
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
        <el-dialog v-model="dialogVisible" title="添加或修改场地" width="460px" center>
            <el-form label-width="120px">

            <el-form-item label="所属场馆">
                <el-select 
                    v-model="court.venueId"  
                    placeholder="请选择场馆"   
                    style="width: 100%;"      
                    clearable                  
                >
                    <el-option
                        v-for="item in venueLists"
                        :key="item.id"
                        :label="item.venueName"  
                        :value="item.id"        
                    />
                </el-select>
            </el-form-item>

                <el-form-item label="场地名称">
                    <el-input v-model="court.name" placeholder="请输入场地名称"/>
                </el-form-item>
                <el-form-item label="场地类型">
                    <el-input  v-model="court.type" placeholder="请输入场地类型"/>
                </el-form-item>
                <el-form-item label="场地容量">
                    <el-input  v-model="court.capacity" placeholder="请输入场地容量"/>
                </el-form-item>
                <el-form-item label="场地价格">
                    <el-input 
                        v-model="court.price" 
                        placeholder="请输入场地价格"
                        type="number"
                        oninput="value=value.replace(/[^0-9]/g,'')"
                    />
                </el-form-item>
                
                <el-form-item class="dialog-btn-wrap">
                    <el-button type="primary" @click="submit">提交</el-button>
                    <el-button @click="dialogVisible = false">取消</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>
        
        <!--- 场馆表格数据 -->
        <el-table :data="list" style="width: 100%">
            <el-table-column prop="venueName" label="场馆名字" width="180" />
            <el-table-column prop="name" label="场地名称" width="180" />
            <el-table-column prop="type" label="场地类型" width="180" />
            <el-table-column prop="capacity" label="场地容量" width="180" />
            <el-table-column prop="price" label="场地价格" width="180" />
            <el-table-column prop="location" label="场地位置" width="300" />
            <el-table-column prop="phone" label="场地位置" width="180" />
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column label="操作" align="center" width="280" #default="scope">
            <el-button type="primary" size="small" @click="editShow(scope.row)">
                修改
            </el-button>
            <el-button type="danger" size="small" @click="deleteById(scope.row)">
                删除
            </el-button>
            </el-table-column>
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
import { GetCourtListByPage,saveCourt,updateCourt,DeleteCourtById } from '@/api/Court';
import { GetAllVenue } from '@/api/Venue';
import { ElMessage, ElMessageBox } from 'element-plus';

//--------------------------------------分页查询功能--------------------------------------
// 分页条总记录数
let total = ref(0)

// 定义表格数据模型
let list = ref([])

let venueLists = ref([])

const pageParamsForm = {
    page: 1, //页码
    limit: 3, //每页记录数
}

const pageParams = ref(pageParamsForm)

// 搜索表单数据
const queryDto = ref({
    name: "" ,
    type: "",
})

// onMounted钩子函数
onMounted(() => {
    fetchData();
    loadVenueList();
})

// 搜索按钮点击事件处理函数
const searchVenue = () => {
    fetchData();
}

// 远程调用后端分页查询接口
const fetchData = async () => {
    const {data , code , message } = await GetCourtListByPage(pageParams.value.page , pageParams.value.limit , queryDto.value) ;
    list.value = data.list;
    total.value = data.total;
}

//封装场馆列表加载函数（单独抽离，避免冗余）
const loadVenueList = async () => {
    const response = await GetAllVenue();
    const data = response?.data || {};
    venueLists.value = data.allVenue;
};

//--------------------------------------重置功能--------------------------------------
// 重置按钮点击事件处理函数
const resetData = async () => {
    queryDto.value = {}
    pageParams.value.page = 1
    await fetchData();
    ElMessage.success('重置成功');
}

//-------------------------------------添加或修改功能--------------------------------------
// 控制对话是否展示的变量
const dialogVisible = ref(false)

// 修改按钮点击事件处理函数
const editShow = (row) => {
    court.value = {...row}
    dialogVisible.value = true
}

//表单数据模型
const defaultForm = {
    id: "",
    venueId: "", // 所属场馆ID（关键：绑定下拉选中值）
    name: "",
    type: "",
    capacity: "",
    price: "",
}
const court = ref(defaultForm) 

//进入添加
const addShow = () => {
    court.value = {};
    dialogVisible.value = true;
};



// 提交按钮事件处理函数
const submit = async () => {
    if(!court.value.id) {
        const {code , message , data} = await saveCourt(court.value) 
        if(code === 200) {
            dialogVisible.value = false
            ElMessage.success('新增场地成功')
            fetchData()
        }else {
            ElMessage.error(message)
        }
    }else {
        const {code , message , data} = await updateCourt(court.value) 
        if(code === 200) {
            dialogVisible.value = false
            ElMessage.success('修改场地成功')
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
       const {code } = await DeleteCourtById(row.id)
       if(code === 200) {
            ElMessage.success('删除场地成功')
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

</style>