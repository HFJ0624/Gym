<template>
    <div class="search-div">
        <!-- 搜索表单 - 重点放大优化 -->
        <el-form label-width="80px" size="default" class="search-form">
            <el-row :gutter="24">
                <el-col :span="8">
                    <el-form-item label="商品名称" class="form-item-lg">
                        <el-input 
                            v-model="queryDto.goodsName"
                            style="width: 100%"
                            placeholder="请输入商品名称"
                            clearable
                            size="default"
                            class="input-lg"
                        ></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="商品类型" class="form-item-lg">
                        <el-select 
                            v-model="queryDto.category"
                            style="width: 100%"
                            placeholder="请选择商品类型"
                            clearable
                            size="default"
                            class="input-lg"
                        >
                            <el-option label="饮品" :value="1"></el-option>
                            <el-option label="零食" :value="2"></el-option>
                            <el-option label="日用品" :value="3"></el-option>
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="24">
                    <el-form-item>
                        <el-button type="primary" @click="searchBeverage" icon="Search">
                            搜索商品
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
        <el-dialog v-model="dialogVisible" title="添加或修改商品" width="560px" center class="venue-dialog">
            <el-form label-width="120px" class="dialog-form" :model="goods">
                <el-row :gutter="20">
                    <el-col :span="12">
                        <el-form-item label="商品名称" prop="goodsName">
                            <el-input v-model="goods.goodsName" placeholder="请输入商品名称"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="商品类型" prop="category">
                            <el-select v-model="goods.category" placeholder="请选择商品类型" style="width: 100%;">
                                <el-option label="饮品" :value="1"></el-option>
                                <el-option label="零食" :value="2"></el-option>
                                <el-option label="日用品" :value="3"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    
                    <el-col :span="12">
                        <el-form-item label="商品价格" prop="price">
                            <el-input-number 
                                v-model="goods.price" 
                                :min="0" 
                                :precision="2" 
                                :step="1"
                                placeholder="请输入商品价格"
                                style="width: 100%;"
                            />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="商品成本" prop="cost">
                            <el-input-number 
                                v-model="goods.cost" 
                                :min="0" 
                                :precision="2" 
                                :step="1"
                                placeholder="请输入商品成本"
                                style="width: 100%;"
                            />
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                    <el-form-item label="商品库存量" prop="stock">
                        <el-input-number 
                            v-model="goods.stock" 
                            :min="0"
                            :step="1"
                            placeholder="请输入商品库存量"
                            style="width: 100%;"
                        />
                    </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="备注" prop="remark">
                            <el-input v-model="goods.remark" placeholder="请输入备注" type="textarea" :rows="3"/>
                        </el-form-item>
                    </el-col>

                    <el-form-item label="商品图片" prop="image">
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
                            v-if="goods.image" 
                            :src="goods.image" 
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
                </el-row>
                
                <el-form-item>
                    <el-button type="primary" @click="submit">提交</el-button>
                    <el-button @click="dialogVisible = false">取消</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>
        
        <!--- 商品表格数据 -->
        <el-table 
            :data="list" 
            style="width: 100%" 
            border 
            stripe 
            hover 
            class="goods-table"
            :empty-text="total === 0 ? '暂无商品数据' : '加载中...'"
        >
            <el-table-column prop="goodsName" label="商品名称" min-width="150" />
            <el-table-column prop="category" label="商品类型" min-width="150" #default="scope">
                <el-if v-if="scope.row.category === 1">饮品</el-if>
                <el-if v-else-if="scope.row.category === 2">零食</el-if>
                <el-if v-else-if="scope.row.category === 3">日用品</el-if>
                <el-if v-else>未知</el-if>
            </el-table-column>
            <el-table-column prop="price" label="商品价格(元)" min-width="180" />
            <el-table-column prop="cost" label="商品成本(元)" min-width="150" />
            <el-table-column prop="stock" label="商品库存量" min-width="120" align="center" />
            <el-table-column prop="image" label="商品图片" #default="scope">
                <img :src="scope.row.image" width="50" />
            </el-table-column>
            <el-table-column prop="status" label="商品状态" min-width="180" #default="scope">
                <el-if v-if="scope.row.status === 1">上架</el-if>
                <el-if v-else-if="scope.row.status === 2">下架</el-if>
                <el-if v-else>未知</el-if>
            </el-table-column>
            <el-table-column prop="remark" label="商品备注" min-width="180" />
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
            </el-table-column>        </el-table>

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
import { GetBeverageListByPage,SaveBeverage,UpdateBeverage,DeleteBeverageById } from '@/api/Beverage';
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
    goodsName: "" ,
    category: ""
})

onMounted(() => {
    fetchData();
})

const searchBeverage = () => {
    fetchData();
}

const fetchData = async () => {
    const {data , code , message } = await GetBeverageListByPage(pageParams.value.page , pageParams.value.limit , queryDto.value) ;
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

//-------------------------------------添加或修改功能--------------------------------------
const dialogVisible = ref(false)

const addShow = () => {
    goods.value = {}
  	dialogVisible.value = true
}

const editShow = (row) => {
    goods.value = {...row}
    dialogVisible.value = true
}

const getCategoryText = (category) => {
    const categoryMap = {
        1: '饮品',
        2: '零食',
        3: '日用品'
    }
    return categoryMap[category] || category
}

const defaultForm = {
    id: "",
    goodsName: "",
    category: "",
    price: 0,
    cost: 0,
    stock: 0,
    image: "",
    status: "",
    remark: ""
}
const goods = ref(defaultForm)

const submit = async () => {
    if(!goods.value.id) {
        const {code , message , data} = await SaveBeverage(goods.value) 
        if(code === 200) {
            dialogVisible.value = false
            ElMessage.success('新增商品成功')
            fetchData()
        }else {
            ElMessage.error(message)
        }
    }else {
        const {code , message , data} = await UpdateBeverage(goods.value) 
        if(code === 200) {
            dialogVisible.value = false
            ElMessage.success('修改商品成功')
            fetchData()
        }else {
            ElMessage.error(message)
        }   
    }    
}

//-------------------------------------删除商品功能--------------------------------------
const deleteById = (row) => {
    ElMessageBox.confirm('此操作将永久删除该记录, 是否继续?', 'Warning', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
       const {code } = await DeleteBeverageById(row.id)
       if(code === 200) {
            ElMessage.success('删除商品成功')
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
    goods.value.image = response.data
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
