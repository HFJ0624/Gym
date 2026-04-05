<template>
  <div class="page">
    <el-card shadow="never">
      <template #header>
        <div class="header-content">
          <span>个人中心</span>
          <el-button type="primary" @click="openDrawer">编辑资料</el-button>
        </div>
      </template>

      <div class="profile">
        <el-avatar :size="56" :src="avatar" />
        <div class="info">
          <div class="name">{{ realName }}</div>
          <div class="sub">手机号：{{ phone }}</div>
          <div class="sub">用户性别：{{ sex }}</div>
        </div>
      </div>

      <el-divider />

      <el-descriptions :column="1" border>
        <el-descriptions-item label="默认联系人">{{ realName }}</el-descriptions-item>
        <el-descriptions-item label="默认手机号">{{ phone }}</el-descriptions-item>
        <el-descriptions-item label="电子邮件">{{ email }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card shadow="never" class="collected-card">
      <template #header>
        <div class="header-content">
          <span>我的收藏</span>
        </div>
      </template>

      <div class="collected-venues" v-loading="collectedLoading">
        <div v-if="collectedVenues.length === 0" class="empty-tip">
          <el-empty description="暂无收藏的场馆" />
        </div>
        <div v-else class="grid">
          <div 
            v-for="venue in collectedVenues" 
            :key="venue.venueId || venue.id" 
            class="venue-card"
            @click="goToVenue(venue.venueId || venue.id)"
          >
            <img class="cover" :src="venue.avatar" :alt="venue.venueName" />
            <div class="body">
              <div class="name">{{ venue.venueName }}</div>
              <div class="meta">
                <span class="tag">{{ venue.venueType }}</span>
                <span>容量：{{ venue.capacity }}人</span>
              </div>
              <div class="addr">{{ venue.location }}</div>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <el-drawer
      v-model="drawerVisible"
      title="编辑个人信息"
      size="500px"
      :before-close="handleClose"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="性别" prop="sex">
          <el-radio-group v-model="form.sex">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 8. 头像上传 -->
        <el-form-item label="头像" prop="avatar" class="form-item">
          <el-upload
            class="avatar-uploader"
            action="http://localhost:9601/admin/system/fileUpload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            :headers="headers"
          >
            <img v-if="form.avatar" :src="form.avatar" class="avatar" />
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-divider content-position="left">修改密码（留空则不修改）</el-divider>
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="form.oldPassword" type="password" placeholder="请输入旧密码" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="form.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请确认新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="drawer-footer">
          <el-button @click="handleClose">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">提交</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, ref, reactive, onMounted } from 'vue'
import { useAuth } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/frontRequest'
import { UpdateUser } from '@/api/profile'
import { GetUserCollectedVenues } from '@/api/venues'

const auth = useAuth()
const router = useRouter()

const realName = computed(() => auth.user?.realName)
const phone = computed(() => auth.user?.phone)
const sex = computed(() => auth.user?.sex)
const email = computed(() => auth.user?.email)
const avatar = computed(() => auth.user?.avatar)

const drawerVisible = ref(false)
const formRef = ref(null)
const submitting = ref(false)

const collectedVenues = ref([])
const collectedLoading = ref(false)

const loadCollectedVenues = async () => {
  collectedLoading.value = true
  try {
    const res = await GetUserCollectedVenues()
    if (res.data && res.data.venueList) {
      collectedVenues.value = res.data.venueList
    }
  } catch (error) {
    console.error('获取收藏列表失败:', error)
  } finally {
    collectedLoading.value = false
  }
}

const goToVenue = (venueId) => {
  router.push(`/venues/court/${venueId}`)
}

const form = reactive({
  realName: '',
  phone: '',
  sex: '男',
  email: '',
  oldPassword: '',
  newPassword: '',
  confirmPassword: '',
  avatar: '',
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  sex: [{ required: true, message: '请选择性别', trigger: 'change' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const openDrawer = () => {
  form.realName = auth.user?.realName || ''
  form.phone = auth.user?.phone || ''
  form.sex = auth.user?.sex || '男'
  form.email = auth.user?.email || ''
  form.avatar = auth.user?.avatar || ''
  form.oldPassword = ''
  form.newPassword = ''
  form.confirmPassword = ''
  drawerVisible.value = true
}

const handleClose = () => {
  ElMessageBox.confirm('确认关闭？未保存的更改将丢失', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    drawerVisible.value = false
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const submitData = {
          id: auth.user?.id,
          realName: form.realName,
          phone: form.phone,
          sex: form.sex,
          email: form.email,
          avatar: form.avatar,
          oldPassword:'',
          newPassword:'',
        }
        
        if (form.oldPassword && form.newPassword) {
          submitData.oldPassword = form.oldPassword
          submitData.newPassword = form.newPassword
        }
        
        const { code, message } = await UpdateUser(submitData)
        
        if (code === 200) {
          ElMessage.success('修改成功')
          auth.setUser({ ...auth.user, ...submitData })
          drawerVisible.value = false
        } else {
          ElMessage.error(message || '修改失败')
        }
      } catch (error) {
        console.error('修改失败:', error)
        ElMessage.error('修改失败，请稍后重试')
      } finally {
        submitting.value = false
      }
    }
  })
}

//-------------------------------------------上传头像验证和方法--------------------------------
// 头像上传成功回调（修复缺失的方法）
const handleAvatarSuccess = (response, file, fileList) => {
  // 假设后端返回的图片地址在response.data.url
  form.avatar = response.data.url;
  ElMessage.success('头像上传成功');
};

// 头像上传前验证（修复缺失的方法）
const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/');
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error('只能上传图片格式！');
    return false;
  }
  if (!isLt2M) {
    ElMessage.error('头像大小不能超过2MB！');
    return false;
  }
  return true;
};

onMounted(() => {
  loadCollectedVenues()
})
</script>

<style scoped lang="scss">
.page {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 18px;
}
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.profile {
  display: flex;
  gap: 14px;
  align-items: center;
}
.info .name {
  font-size: 18px;
  font-weight: 900;
  color: #1f2d3d;
}
.info .sub {
  margin-top: 6px;
  color: #606266;
  font-size: 13px;
}
.hint {
  margin-top: 12px;
  font-size: 12px;
  color: #909399;
  line-height: 1.6;
}
.drawer-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
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

.collected-card {
  margin-top: 20px;
}

.collected-venues {
  min-height: 100px;
}

.empty-tip {
  padding: 40px 0;
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.venue-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.06);
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.venue-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.12);
}

.venue-card .cover {
  width: 100%;
  height: 160px;
  object-fit: cover;
}

.venue-card .body {
  padding: 12px 12px 14px;
}

.venue-card .name {
  font-size: 16px;
  font-weight: 800;
  color: #1f2d3d;
  margin-bottom: 8px;
}

.venue-card .meta {
  display: flex;
  gap: 10px;
  color: #909399;
  font-size: 12px;
  margin-bottom: 6px;
  flex-wrap: wrap;
}

.venue-card .meta .tag {
  background: #ecf5ff;
  color: #409eff;
  padding: 1px 8px;
  border-radius: 999px;
}

.venue-card .addr {
  color: #606266;
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>

