<template>
    <div class="search-div">
        <!-- 搜索表单 -->
        <el-form label-width="70px" size="small">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="模块标题">
                        <el-input v-model="queryDto.title"
                                style="width: 100%"
                                placeholder="请输入模块标题"
                                ></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="操作人员">
                        <el-input v-model="queryDto.operaName"
                                style="width: 100%"
                                placeholder="请输入操作人员"
                                ></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="业务类型">
                        <el-input v-model="queryDto.type"
                                style="width: 100%"
                                placeholder="请输入业务类型"
                                ></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="请求方式">
                        <el-input v-model="queryDto.requestMethod"
                                style="width: 100%"
                                placeholder="请输入请求方式"
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

        
        <!--- 场馆表格数据 -->
        <el-table :data="list" style="width: 100%">
            <el-table-column prop="title" label="模块标题" width="180" />
            <el-table-column prop="operaName" label="操作人员" width="180" />
            <el-table-column prop="requestMethod" label="请求方式" width="180" />
            <el-table-column prop="operatorType" label="操作类别" width="180" />
            <el-table-column prop="operaUrl" label="请求URL" width="180" />
            <el-table-column prop="operaIp" label="主机地址" width="300" />
            <el-table-column prop="jsonResult" label="返回参数" width="180" />
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column label="操作" align="center" width="280" #default="scope">
            <el-button type="danger" size="small"  @click="deleteById(scope.row)">
                删除
            </el-button>
            </el-table-column>
        </el-table>

        <!--分页条-->
        <el-pagination
                v-model:current-page="pageParams.page"
                v-model:page-size="pageParams.limit"
                :page-sizes="[10, 20, 50, 100]"
                @size-change="fetchData"
                @current-change="fetchData"
                layout="total, sizes, prev, pager, next"
                :total="total"
                />
  </div>

</template>

<script setup>
import { ref , onMounted } from 'vue'; 
import { GetLogListByPage,DeleteOperaLogById } from '@/api/OperaLog';
import { ElMessage,ElMessageBox } from 'element-plus';

//--------------------------------------分页查询功能--------------------------------------
// 分页条总记录数
let total = ref(0)

// 定义表格数据模型
let list = ref([])

const pageParamsForm = {
    page: 1, //页码
    limit: 20, //每页记录数
}

const pageParams = ref(pageParamsForm)

// 搜索表单数据
const queryDto = ref({
    title: "",
    operaName: "" ,
    type: "",
    requestMethod: "",
})

// onMounted钩子函数
onMounted(() => {
    fetchData();
})

// 搜索按钮点击事件处理函数
const searchVenue = () => {
    fetchData();
}

// 远程调用后端分页查询接口
const fetchData = async () => {
    const {data , code , message } = await GetLogListByPage(pageParams.value.page , pageParams.value.limit , queryDto.value) ;
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

//------------------------删除日志功能---------------------------------
const deleteById = (row) => {
    ElMessageBox.confirm('此操作将永久删除该记录, 是否继续?', 'Warning', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
       const {code } = await DeleteOperaLogById(row.id)
       if(code === 200) {
            ElMessage.success('删除操作日志成功')
            fetchData()
       }
    })
}

</script>

<style>

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

</style>