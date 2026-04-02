<template>
    <div class="search-div">
        <!-- 搜索表单 -->
        <el-form label-width="70px" size="small">
            <el-row>
                <el-col :span="12">
                    <el-form-item label="体育馆名字" label-width="10">
                        <el-input v-model="queryDto.gymName"
                                style="width: 100%"
                                placeholder="请输入体育馆名字"
                                ></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="器械名字">
                        <el-input v-model="queryDto.name"
                                style="width: 100%"
                                placeholder="请输入器械名字"
                                ></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="器械品牌">
                        <el-input v-model="queryDto.brand"
                                style="width: 100%"
                                placeholder="请输入器械品牌"
                                ></el-input>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row style="display:flex">
                <el-button type="primary" size="small" @click="searchEquipment">
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
                <el-form-item label="健身房名称">
                    <el-input v-model="equipment.gymName" placeholder="请输入健身房名称"/>
                </el-form-item>
                <el-form-item label="器械名称">
                    <el-input  v-model="equipment.name" placeholder="请输入器械名称"/>
                </el-form-item>
                <el-form-item label="器械品牌">
                    <el-input  v-model="equipment.brand" placeholder="请输入器械品牌"/>
                </el-form-item>

                <el-form-item label="器械价格">
                    <el-input 
                        v-model="equipment.price" 
                        placeholder="请输入器械价格"
                        type="number"
                        oninput="value=value.replace(/[^0-9]/g,'')"
                    />
                </el-form-item>
                <el-form-item label="器械数量">
                    <el-input 
                        v-model="equipment.quantity" 
                        placeholder="请输入器械数量"
                        type="number"
                        oninput="value=value.replace(/[^0-9]/g,'')"
                    />
                </el-form-item>
                <el-form-item label="器械描述">
                    <el-input  v-model="equipment.description" placeholder="请输入器械描述" type="textarea" :rows="3"/>
                </el-form-item>

                <el-form-item label="头像">
                    <el-upload
                            class="avatar-uploader"
                            action="http://localhost:9601/admin/system/fileUpload"
                            :show-file-list="false"
                            :on-success="handleAvatarSuccess"
                            :headers="headers"
                            >
                        <img v-if="equipment.imageUrl" :src="equipment.imageUrl" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                    </el-upload>
                </el-form-item>

                <el-form-item label="购买日期">
                    <el-date-picker v-model="equipment.purchaseDate"
                                    type="datetime"
                                    placeholder="结束营业时间"
                                    format="YYYY-MM-DD"
                                    value-format="YYYY-MM-DD"
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
            <el-table-column prop="gymName" label="体育馆名字" width="180" />
            <el-table-column prop="name" label="器械名字" width="180" />
            <el-table-column prop="brand" label="器械品牌" width="180" />
            <el-table-column prop="price" label="器械价格" width="180" />
            <el-table-column prop="quantity" label="器械数量" width="180" />

            <el-table-column prop="imageUrl" label="头像" #default="scope" width="180">
                <img :src="scope.row.imageUrl" width="50" />
            </el-table-column>

            <el-table-column prop="description" label="描述" width="300" />
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
import { GetEquipmentListByPage,saveEquipment,updateEquipment,DeleteEquipmentById } from '@/api/Equipment';
import { ElMessage, ElMessageBox } from 'element-plus';

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
    gymName: "",
    name: "" ,
    type: "",
})

// onMounted钩子函数
onMounted(() => {
    fetchData();
})

// 搜索按钮点击事件处理函数
const searchEquipment = () => {
    fetchData();
}

// 远程调用后端分页查询接口
const fetchData = async () => {
    const {data , code , message } = await GetEquipmentListByPage(pageParams.value.page , pageParams.value.limit , queryDto.value) ;
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

//-------------------------------------添加或修改功能--------------------------------------
// 控制对话是否展示的变量
const dialogVisible = ref(false)

// 修改按钮点击事件处理函数
const editShow = (row) => {
    equipment.value = {...row}
    dialogVisible.value = true
}

//表单数据模型
const defaultForm = {
    id: "",
    gymName: "",
    name: "",
    brand: "",
    price: "",
    quantity: "",
    description: "",
    imageUrl: "",
    purchaseDate: "",
}
const equipment = ref(defaultForm) 

//进入添加
const addShow = () => {
    equipment.value = {};
    dialogVisible.value = true;
};



// 提交按钮事件处理函数
const submit = async () => {
    if(!equipment.value.id) {
        const {code , message , data} = await saveEquipment(equipment.value) 
        if(code === 200) {
            dialogVisible.value = false
            ElMessage.success('新增器械成功')
            fetchData()
        }else {
            ElMessage.error(message)
        }
    }else {
        const {code , message , data} = await updateEquipment(equipment.value) 
        if(code === 200) {
            dialogVisible.value = false
            ElMessage.success('修改器械成功')
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
       const {code } = await DeleteEquipmentById(row.id)
       if(code === 200) {
            ElMessage.success('删除器械成功')
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
    equipment.value.imageUrl = response.data
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