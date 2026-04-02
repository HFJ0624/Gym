<template>
    <div class="search-div">
        <!-- 搜索表单 -->
        <el-form label-width="70px" size="small">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="用户名称">
                        <el-input v-model="queryDto.userName"
                                style="width: 100%"
                                placeholder="请输入用户名"
                                ></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="真实名称">
                        <el-input v-model="queryDto.realName"
                                style="width: 100%"
                                placeholder="请输入真实姓名"
                                ></el-input>
                    </el-form-item>
                </el-col>

                <el-col :span="12">
                    <el-form-item label="场馆名称">
                        <el-input v-model="queryDto.venueName"
                                style="width: 100%"
                                placeholder="请输入场馆名"
                                ></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="场馆地址">
                        <el-input v-model="queryDto.location"
                                style="width: 100%"
                                placeholder="请输入场馆地址"
                                ></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row style="display:flex">
                <el-button type="primary" size="small" @click="searchVenueBook">
                    搜索
                </el-button>
                <el-button size="small" @click="resetData">重置</el-button>
            </el-row>
        </el-form>

        
        <!--- 场馆表格数据 -->
        <el-table :data="list" style="width: 100%">
            <el-table-column prop="orderNo" label="订单编号" width="180" />
            <el-table-column prop="userName" label="用户名" width="180" />
            <el-table-column prop="realName" label="真实姓名" width="180" />
            <el-table-column prop="venueName" label="场馆名称" width="180" />
            <el-table-column prop="location" label="场馆地址" width="300" />
            <el-table-column prop="personNum" label="总人数" width="180" />
            <el-table-column prop="bookingDate" label="预约日期" width="180" />
            <el-table-column prop="remark" label="备注" width="180" />
            <el-table-column prop="venueType" label="场馆类型" width="180" />
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column label="操作" align="center" width="280" #default="scope">
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
import { GetVenueBookListByPage,DeleteVenueBookById } from '@/api/VenueBook';
import { ElMessage,ElMessageBox } from 'element-plus';

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
    userName: "",
    realName: "",
    venueName: "" ,
    location: "",
})

// onMounted钩子函数
onMounted(() => {
    fetchData();
})

// 搜索按钮点击事件处理函数
const searchVenueBook = () => {
    fetchData();
}

// 远程调用后端分页查询接口
const fetchData = async () => {
    const {data , code , message } = await GetVenueBookListByPage(pageParams.value.page , pageParams.value.limit , queryDto.value) ;
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


//------------------------删除预约场地功能---------------------------------
const deleteById = (row) => {
    ElMessageBox.confirm('此操作将永久删除该记录, 是否继续?', 'Warning', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
       const {code } = await DeleteVenueBookById(row.id)
       if(code === 200) {
            ElMessage.success('删除预约场馆成功')
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