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
        <el-descriptions-item label="账户余额">
          <span class="balance-amount">¥{{ balance }}</span>
          <el-button type="primary" size="small" @click="openRechargeDialog" style="margin-left: 12px;">充值</el-button>
        </el-descriptions-item>
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

    <el-dialog v-model="rechargeDialogVisible" title="账户充值" width="450px">
      <div class="recharge-content">
        <div class="recharge-tip">请选择充值金额</div>
        <div class="amount-options">
          <div
            v-for="amount in [100, 200, 500, 1000]"
            :key="amount"
            class="amount-option"
            :class="{ active: selectedAmount === amount }"
            @click="selectAmount(amount)"
          >
            ¥{{ amount }}
          </div>
          <div class="amount-option custom" :class="{ active: selectedAmount === 'custom' }" @click="selectAmount('custom')">
            <el-input-number
              v-if="selectedAmount === 'custom'"
              v-model="customAmount"
              :min="1"
              :max="99999"
              :step="10"
              size="small"
              controls-position="right"
              style="width: 100px;"
              @click.stop
            />
            <span v-else>自定义</span>
          </div>
        </div>
        <div class="recharge-summary">
          <span>充值金额：</span>
          <span class="total-amount">¥{{ finalAmount }}</span>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="rechargeDialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="recharging" @click="handleRecharge">确认充值</el-button>
        </div>
      </template>
    </el-dialog>

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
import { UpdateUser, GetBalance, Recharge } from '@/api/profile'
import { GetUserCollectedVenues } from '@/api/venues'

const auth = useAuth()
const router = useRouter()

const realName = computed(() => auth.user?.realName)
const phone = computed(() => auth.user?.phone)
const sex = computed(() => auth.user?.sex)
const email = computed(() => auth.user?.email)
const avatar = computed(() => auth.user?.avatar)
const balance = ref(0)

const drawerVisible = ref(false)
const formRef = ref(null)
const submitting = ref(false)

const rechargeDialogVisible = ref(false)
const selectedAmount = ref(100)
const customAmount = ref(100)
const recharging = ref(false)

const collectedVenues = ref([])
const collectedLoading = ref(false)

const finalAmount = computed(() => {
  if (selectedAmount.value === 'custom') {
    return customAmount.value || 0
  }
  return selectedAmount.value
})

const loadBalance = async () => {
  try {
    const userId = auth.user?.id
    if (!userId) return
    const res = await GetBalance(userId)
    if (res.code === 200 && res.data && res.data.balance) {
      balance.value = res.data.balance.balance || 0
    }
  } catch (error) {
    console.error('获取余额失败:', error)
  }
}

const openRechargeDialog = () => {
  selectedAmount.value = 100
  customAmount.value = 100
  rechargeDialogVisible.value = true
}

const selectAmount = (amount) => {
  selectedAmount.value = amount
}

const handleRecharge = async () => {
  const amount = finalAmount.value
  if (!amount || amount <= 0) {
    ElMessage.warning('请选择充值金额')
    return
  }

  try {
    recharging.value = true
    const res = await Recharge({ 
      userId: auth.user?.id,
      amount: amount 
    })

    if (res.code === 200) {
      ElMessage.success('充值成功')
      await loadBalance()
      rechargeDialogVisible.value = false
    } else {
      ElMessage.error(res.message || '充值失败')
    }
  } catch (error) {
    console.error('充值失败:', error)
    ElMessage.error('充值失败，请稍后重试')
  } finally {
    recharging.value = false
  }
}

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
  loadBalance()
})
</script>

<style scoped lang="scss">
.page {
  max-width: 900px;
  margin: 0 auto;
  padding: 0 18px;
}

.balance-amount {
  font-size: 20px;
  font-weight: 700;
  color: #f7ba2a;
}

.recharge-content {
  padding: 10px 0;
}

.recharge-tip {
  font-size: 14px;
  color: #666;
  margin-bottom: 16px;
}

.amount-options {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-bottom: 24px;
}

.amount-option {
  border: 1px solid #e5e5e5;
  border-radius: 4px;
  padding: 16px 12px;
  text-align: center;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  color: #1a1a1a;

  &:hover {
    border-color: #1a1a1a;
  }

  &.active {
    border-color: #f7ba2a;
    background: #fffbe6;
    color: #f7ba2a;
  }

  &.custom {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.recharge-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: #f5f5f5;
  border-radius: 4px;
  font-size: 14px;

  .total-amount {
    font-size: 24px;
    font-weight: 700;
    color: #f7ba2a;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
:deep(.el-card) {
  border: 1px solid #e5e5e5;
  border-radius: 0;
}
:deep(.el-card__header) {
  border-bottom: 1px solid #e5e5e5;
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
  font-weight: 700;
  color: #1a1a1a;
}
.info .sub {
  margin-top: 6px;
  color: #666;
  font-size: 13px;
}
.hint {
  margin-top: 12px;
  font-size: 12px;
  color: #888;
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
  filter: grayscale(10%);
}
.avatar-uploader .el-upload {
  border: 1px dashed #1a1a1a;
  border-radius: 0;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: #1a1a1a;
  background: #f5f5f5;
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #666;
  width: 178px;
  height: 178px;
  text-align: center;
}

:deep(.el-button--primary) {
  background: #1a1a1a;
  border-color: #1a1a1a;
  border-radius: 0;
}
:deep(.el-button--default) {
  border-color: #1a1a1a;
  border-radius: 0;
}
:deep(.el-input__wrapper) {
  border-radius: 0;
}
:deep(.el-select .el-input__wrapper) {
  border-radius: 0;
}
:deep(.el-radio__label) {
  color: #1a1a1a;
}
:deep(.el-switch__label) {
  color: #1a1a1a;
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
  gap: 20px;
}

.venue-card {
  background: #fff;
  border: 1px solid #e5e5e5;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.25s ease;
}

.venue-card:hover {
  transform: translateY(-4px);
  border-color: #1a1a1a;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.08);
}

.venue-card .cover {
  width: 100%;
  height: 180px;
  object-fit: cover;
  filter: grayscale(20%);
}

.venue-card .body {
  padding: 20px;
}

.venue-card .name {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 10px;
}

.venue-card .meta {
  display: flex;
  gap: 12px;
  color: #666;
  font-size: 13px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.venue-card .meta .tag {
  background: #f5f5f5;
  color: #1a1a1a;
  padding: 4px 10px;
  font-weight: 500;
}

.venue-card .addr {
  color: #666;
  font-size: 13px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>

