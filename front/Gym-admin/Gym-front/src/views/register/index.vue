<template>
  <div class="login-page">
    <!-- 左侧：和登录页完全一致 大号灵动玩偶 -->
    <div class="left-side">
      <div class="logo-text">体育场馆预约系统</div>

      <div class="character-group"
        :class="{
          peek: focusUsername,
          hide: focusPassword
        }"
      >
        <div class="char char1">
          <div class="eye left"></div>
          <div class="eye right"></div>
        </div>
        <div class="char char2">
          <div class="eye left"></div>
          <div class="eye right"></div>
        </div>
        <div class="char char3">
          <div class="eye left"></div>
          <div class="eye right"></div>
        </div>
        <div class="char char4">
          <div class="eye left"></div>
          <div class="eye right"></div>
        </div>
      </div>
    </div>

    <!-- 右侧：你的注册表单（功能完全不动） -->
    <div class="right-side">
      <div class="card">
        <div class="brand">
          <div class="title">体育场馆预约</div>
          <div class="sub">用户注册</div>
        </div>

        <el-form 
          class="login-form" 
          ref="registerFormRef" 
          :model="formData" 
          label-width="80px"
          size="large"
        >
          <el-form-item label="用户名" prop="username" class="form-item">
            <el-input
              class="input-item"
              v-model="formData.username"
              prefix-icon="User"
              clearable
              placeholder="请输入用户名"
              @focus="focusUsername = true"
              @blur="focusUsername = focusPassword = false"
            />
          </el-form-item>

          <el-form-item label="密码" prop="password" class="form-item">
            <el-input
              class="input-item"
              v-model="formData.password"
              prefix-icon="Lock"
              show-password
              clearable
              placeholder="请输入密码"
              @focus="focusPassword = true"
              @blur="focusUsername = focusPassword = false"
            />
          </el-form-item>

          <el-form-item label="真实姓名" prop="realName" class="form-item">
            <el-input
              class="input-item"
              v-model="formData.realName"
              prefix-icon="UserFilled"
              clearable
              placeholder="请输入真实姓名"
            />
          </el-form-item>

          <el-form-item label="性别" prop="sex" class="form-item">
            <el-radio-group v-model="formData.sex" class="gender-group">
              <el-radio label="男">男</el-radio> 
              <el-radio label="女">女</el-radio> 
            </el-radio-group>
          </el-form-item>

          <el-form-item label="电话" prop="phone" class="form-item">
            <el-input
              class="input-item"
              v-model="formData.phone"
              prefix-icon="Phone"
              clearable
              placeholder="请输入手机号码"
            />
          </el-form-item>

          <el-form-item label="邮箱" prop="email" class="form-item">
            <el-input
              class="input-item"
              v-model="formData.email"
              prefix-icon="Message"
              clearable
              placeholder="请输入邮箱地址"
            />
          </el-form-item>

          <el-form-item label="角色" prop="role" class="form-item">
            <el-select
              class="input-item select"
              v-model="formData.role"
              placeholder="请选择用户角色"
            >
              <el-option label="用户" value="用户" />
            </el-select>
          </el-form-item>

          <el-form-item label="头像" prop="avatar" class="form-item">
            <el-upload
              class="avatar-uploader"
              action="http://localhost:9601/admin/system/fileUpload"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
              :headers="headers"
            >
              <img v-if="formData.avatar" :src="formData.avatar" class="avatar" />
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-form-item>

          <el-form-item label="状态" prop="status" class="form-item">
            <el-switch
              v-model="formData.status"
              active-text="启用"
              inactive-text="禁用"
              active-value="1"
              inactive-value="0"
            />
          </el-form-item>

          <el-form-item class="form-item submit-item">
            <el-button type="primary" size="large" class="btn" @click="handleSubmit">
              提交注册
            </el-button>
          </el-form-item>

          <el-form-item class="form-item register-item">
            <el-button type="text" class="to-login" @click="goToLogin">
              已有账号？去登录
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
// ==============================
// 你的所有注册逻辑 100% 完全不动
// ==============================
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { Register } from '@/api/auth';
import { ElMessage } from 'element-plus';

const router = useRouter();
const headers = ref({});

const defaultFormData = {
  username: "",
  password: "",
  realName: "",
  sex: "",
  phone: "",
  email: "",
  role: "",
  avatar: "",
  status: "1"
}

const formData = ref(defaultFormData);
const registerFormRef = ref(null);

// 交互控制（仅新增）
const focusUsername = ref(false)
const focusPassword = ref(false)

const handleSubmit = async () => {
  try {
    const { code, message } = await Register(formData.value);
    if (code === 200) {
      ElMessage.success('注册成功');
      formData.value = { ...defaultFormData };
      router.replace('/login')
    } else {
      ElMessage.error(message || '注册失败');
    }
  } catch (e) {
    ElMessage.error('注册接口异常');
  }
};

const goToLogin = () => {
  router.replace('/login')
};

const handleAvatarSuccess = (response) => {
  formData.value.avatar = response.data;
  ElMessage.success('头像上传成功');
};

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/');
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isImage) { ElMessage.error('只能上传图片！'); return false }
  if (!isLt2M) { ElMessage.error('不能超过2MB'); return false }
  return true;
};

onMounted(() => {
  formData.value.status = "1";
});
</script>

<style scoped lang="scss">
/* ========= 整体布局 和登录页完全统一 ========= */
.login-page {
  width: 100vw;
  height: 100vh;
  display: flex;
  background: #f6f7f9;
  overflow: hidden;
}

/* ========= 左侧 大号灵动玩偶 ========= */
.left-side {
  flex: 1;
  background: #f6f7f9;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
}

.logo-text {
  font-size: 38px;
  font-weight: 800;
  color: #222;
  margin-bottom: 70px;
}

.character-group {
  display: flex;
  gap: 22px;
  align-items: flex-end;
  transition: all 0.5s cubic-bezier(0.3, 0.9, 0.5, 1);

  &.peek {
    transform: translateX(160px) scale(0.95) rotate(-2.5deg);
    .eye {
      width: 18px;
      height: 18px;
      background: #111;
    }
  }

  &.hide {
    transform: translateX(60px);
    .eye {
      width: 20px;
      height: 4px;
      background: #111;
    }
  }
}

.char {
  position: relative;
  border-radius: 14px;
  .eye {
    position: absolute;
    top: 24%;
    width: 16px;
    height: 16px;
    background: #fff;
    border-radius: 50%;
    transition: all 0.28s ease;
    &.left { left: 24%; }
    &.right { right: 24%; }
  }
}

.char1 { width: 140px; height: 140px; background: #7c3aed; }
.char2 { width: 115px; height: 200px; background: #ff9a6c; }
.char3 { width: 105px; height: 190px; background: #111; }
.char4 { width: 95px; height: 140px; background: #facc15; border-radius: 48px 48px 14px 14px; }

/* ========= 右侧 注册卡片 ========= */
.right-side {
  flex: 1;
  background: #fff;
  display: grid;
  place-items: center;
  padding: 30px;
}

.card {
  width: 520px;
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 10px 35px rgba(0,0,0,0.07);
}

.brand {
  text-align: center;
  margin-bottom: 24px;
}
.title { font-size: 26px; font-weight: 800; color: #1f2d3d; }
.sub { margin-top: 6px; color: #999; font-size: 15px; }

/* 表单样式 */
.login-form {
  .form-item {
    margin-bottom: 16px;
  }
  .input-item {
    --el-input-height: 46px;
  }
  .btn {
    width: 100%;
    height: 46px;
    border-radius: 8px;
    font-size: 16px;
  }
  .to-login {
    color: #409eff;
    font-size: 14px;
  }
  .gender-group {
    display: flex;
    gap: 20px;
  }
  .avatar-uploader {
    display: flex;
    align-items: center;
    .avatar,
    .avatar-uploader-icon {
      width: 100px;
      height: 100px;
      border-radius: 50%;
    }
  }
}

/* 移动端 */
@media (max-width: 768px) {
  .left-side { display: none; }
  .card { width: 90%; }
}
</style>