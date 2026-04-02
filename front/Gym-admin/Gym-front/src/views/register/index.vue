<!-- src/views/login/Register.vue -->
<template>
  <div class="login-container">
    <!-- 背景装饰层（和登录页一致） -->
    <div class="login-bg"></div>
    <!-- 注册卡片（和登录页卡片样式统一） -->
    <div class="login-card">
      <!-- 顶部logo/标题区 -->
      <div class="login-header">
        <div class="login-logo">
          <i class="el-icon-stadium" style="font-size: 32px; color: #409eff;"></i>
        </div>
        <h1 class="login-title">体育场馆预约系统</h1>
        <h2 class="sub-title">用户注册</h2>
      </div>

      <!-- 表单区域（修复数据绑定和验证规则） -->
      <el-form 
        class="login-form" 
        ref="registerFormRef" 
        :model="formData" 
        label-width="80px"
        size="large"
      >
        <!-- 1. 用户名 -->
        <el-form-item label="用户名" prop="username" class="form-item">
          <el-input
            class="input-item"
            v-model="formData.username"
            prefix-icon="User"
            clearable
            placeholder="请输入用户名"
          />
        </el-form-item>

        <!-- 2. 密码 -->
        <el-form-item label="密码" prop="password" class="form-item">
          <el-input
            class="input-item"
            v-model="formData.password"
            prefix-icon="Lock"
            show-password
            clearable
            placeholder="请输入密码"
          />
        </el-form-item>

        <!-- 3. 真实姓名 -->
        <el-form-item label="真实姓名" prop="realName" class="form-item">
          <el-input
            class="input-item"
            v-model="formData.realName"
            prefix-icon="UserFilled"
            clearable
            placeholder="请输入真实姓名"
          />
        </el-form-item>

        <!-- 4. 性别（单选框） -->
        <el-form-item label="性别" prop="sex" class="form-item">
          <el-radio-group v-model="formData.sex" class="gender-group">
            <el-radio label="男">男</el-radio> 
            <el-radio label="女">女</el-radio> 
          </el-radio-group>
        </el-form-item>

        <!-- 5. 电话 -->
        <el-form-item label="电话" prop="phone" class="form-item">
          <el-input
            class="input-item"
            v-model="formData.phone"
            prefix-icon="Phone"
            clearable
            placeholder="请输入手机号码"
          />
        </el-form-item>

        <!-- 6. 邮件 -->
        <el-form-item label="邮箱" prop="email" class="form-item">
          <el-input
            class="input-item"
            v-model="formData.email"
            prefix-icon="Message"
            clearable
            placeholder="请输入邮箱地址"
          />
        </el-form-item>

        <!-- 7. 角色（下拉选择） -->
        <el-form-item label="角色" prop="role" class="form-item">
          <el-select
            class="input-item select"
            v-model="formData.role"
            placeholder="请选择用户角色"
            prefix-icon="UserVip"
          >
            <el-option label="用户" value="用户" />
          </el-select>
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
            <img v-if="formData.avatar" :src="formData.avatar" class="avatar" />
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>

        <!-- 9. 用户状态（开关） -->
        <el-form-item label="状态" prop="status" class="form-item">
          <el-switch
            v-model="formData.status"
            active-text="启用"
            inactive-text="禁用"
            active-value="1"
            inactive-value="0"
          />
        </el-form-item>

        <!-- 注册按钮 -->
        <el-form-item class="form-item submit-item">
          <el-button
            type="primary"
            class="login-btn"
            size="large"
            @click="handleSubmit"
            icon="CircleCheck"
          >
            提交注册
          </el-button>
        </el-form-item>

        <!-- 返回登录按钮 -->
        <el-form-item class="form-item register-item">
          <el-button
            type="text"
            class="register-btn"
            @click="goToLogin"
          >
            <i class="el-icon-user-filled" style="font-size: 16px; color: #409eff; margin-right: 4px;"></i>
            已有账号？去登录
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 底部版权信息（和登录页一致） -->
      <div class="login-footer">
        <span>© 2026 体育场馆预约系统 版权所有</span>
      </div>
    </div>
  </div>

  <!-- 语言切换组件（样式和登录页统一） -->
  <div class="change-lang">
    <change-lang />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router'; // 新增路由依赖
import { Register } from '@/api/auth';
import { ElMessage, ElMessageBox } from 'element-plus';

// 初始化路由
const router = useRouter();

// ------------------------ 表单数据模型（和模板字段完全匹配） ------------------------
const defaultFormData = {
  username: "",        // 匹配模板的userName
  password: "",        // 密码
  realName: "",        // 真实姓名
  sex: "",          // 匹配模板的gender（替代原来的sex）
  phone: "",           // 电话
  email: "",         // 邮箱
  role: "",            // 角色
  avatar: "",          // 头像
  status: "1"          // 默认启用
}

const formData = ref(defaultFormData);

const handleSubmit = async () => {

    // 调用注册接口
    const { code, message, data } = await Register(formData.value);
    if (code === 200) {
      ElMessage.success('注册成功');
      // 重置表单
      formData.value = { ...defaultFormData };
      // 跳转到登录页
      router.replace('/login')
    } else {
      ElMessage.error(message || '注册失败');
    }
};

// 跳转到登录页（修复未定义的方法）
const goToLogin = () => {
  router.replace('/login')
};

// 头像上传成功回调（修复缺失的方法）
const handleAvatarSuccess = (response, file, fileList) => {
  // 假设后端返回的图片地址在response.data.url
  formData.value.avatar = response.data;
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

// 重置表单（和新增用户代码风格一致）
const resetRegisterForm = () => {
  formData.value = { ...defaultFormData };
  if (registerFormRef.value) {
    registerFormRef.value.clearValidate();
  }
};

// 页面初始化（可选）
onMounted(() => {
  // 初始化逻辑（如默认值设置）
  formData.value.status = "1"; // 默认启用
});
</script>

<style lang="scss" scoped>
// 全局容器（和登录页完全一致）
.login-container {
  width: 100vw;
  height: 100vh;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  // 背景层（恢复背景图片，和登录页一致）
  .login-bg {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    // 保留背景图片 + 渐变叠加（和登录页一致）
    background: url('../../assets/background.jpg');
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    // 背景纹理（和登录页一致）
    &::after {
      content: '';
      position: absolute;
      width: 100%;
      height: 100%;
      background-image: 
        radial-gradient(rgba(255, 255, 255, 0.1) 2px, transparent 2px);
      background-size: 40px 40px;
      opacity: 0.3;
    }
  }

  // 注册卡片（和登录页卡片样式完全一致）
  .login-card {
    width: 480px; // 注册表单字段多，适当加宽（登录页420px）
    background: #ffffff;
    border-radius: 16px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
    padding: 40px 30px;
    position: relative;
    z-index: 10;
    border: 1px solid #e5e7eb;

    // 顶部标题区（和登录页一致）
    .login-header {
      text-align: center;
      margin-bottom: 30px;

      .login-logo {
        margin-bottom: 12px;
        display: inline-block;
        width: 60px;
        height: 60px;
        line-height: 60px;
        background: #409eff;
        border-radius: 50%;
        color: #fff;
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
        text-align: center;
      }

      .login-title {
        font-size: 24px;
        font-weight: 600;
        color: #1f2937;
        margin: 0 0 8px 0;
      }

      .sub-title {
        font-size: 18px;
        color: #6b7280;
        margin: 0 0 12px 0;
        font-weight: 500;
      }
    }

    // 表单区域（和登录页样式统一）
    .login-form {
      .form-item {
        margin-bottom: 16px; // 适当减小间距，适配多字段

        &.submit-item {
          margin-bottom: 12px;
        }

        &.register-item {
          margin-bottom: 0;
        }
      }

      // 表单标签样式（和登录页统一）
      :deep(.el-form-item__label) {
        color: #1f2937;
        font-size: 14px;
        font-weight: 500;
      }

      // 输入框样式（和登录页完全一致）
      .input-item {
        :deep(.el-input__wrapper) {
          height: 50px;
          border-radius: 8px;
          border: 1px solid #e5e7eb;
          box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
          transition: all 0.3s ease;
          background: #fff;

          &:hover {
            border-color: #93c5fd;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
          }

          &:focus-within {
            border-color: #409eff;
            box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
          }
        }

        :deep(.el-input__inner) {
          font-size: 15px;
          color: #1f2937;
          height: 100%;
          line-height: 50px;

          &::placeholder {
            color: #9ca3af;
          }
        }

        :deep(.el-input__prefix) {
          color: #9ca3af;
        }
      }

      // 性别单选组样式（适配新UI）
      .gender-group {
        display: flex;
        gap: 20px;
        padding-top: 8px;

        :deep(.el-radio__label) {
          color: #1f2937;
          font-size: 14px;
        }

        :deep(.el-radio__input.is-checked + .el-radio__label) {
          color: #409eff;
        }
      }

      // 下拉选择框样式（和输入框统一）
      .select {
        :deep(.el-select__wrapper) {
          height: 50px;
          border-radius: 8px;
          border: 1px solid #e5e7eb;
          box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
          transition: all 0.3s ease;

          &:hover {
            border-color: #93c5fd;
          }

          &:focus-within {
            border-color: #409eff;
            box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.1);
          }
        }
      }

      // 头像上传样式（适配新UI）
      .avatar-uploader {
        display: flex;
        align-items: center;
        margin-top: 8px;

        .avatar {
          width: 100px;
          height: 100px;
          border-radius: 50%;
          border: 2px solid #e5e7eb;
        }

        .avatar-uploader-icon {
          width: 100px;
          height: 100px;
          line-height: 100px;
          text-align: center;
          border: 1px dashed #e5e7eb;
          border-radius: 50%;
          background: #f9fafb;
          color: #9ca3af;
          font-size: 24px;
          cursor: pointer;
          transition: all 0.2s ease;

          &:hover {
            color: #409eff;
            border-color: #409eff;
          }
        }
      }

      // 开关样式（适配新UI）
      :deep(.el-switch) {
        margin-top: 8px;

        :deep(.el-switch__label) {
          color: #1f2937;
          font-size: 14px;
        }

        :deep(.el-switch__core) {
          border-radius: 12px;
        }
      }

      // 注册按钮（和登录页按钮样式完全一致）
      .login-btn {
        width: 100%;
        height: 50px;
        border-radius: 8px;
        background: #409eff;
        border: none;
        font-size: 16px;
        font-weight: 500;
        transition: all 0.3s ease;

        &:hover {
          background: #3390ff;
          transform: translateY(-2px);
          box-shadow: 0 6px 16px rgba(64, 158, 255, 0.3);
        }

        &:active {
          transform: translateY(0);
        }
      }

      // 返回登录按钮（和登录页注册按钮样式一致）
      .register-btn {
        color: #409eff;
        font-size: 14px;
        padding: 0;
        transition: all 0.2s ease;

        &:hover {
          color: #3390ff;
          text-decoration: underline;
        }
      }
    }

    // 底部版权（和登录页一致）
    .login-footer {
      margin-top: 30px;
      text-align: center;
      font-size: 12px;
      color: #9ca3af;
    }
  }
}

// 语言切换样式（和登录页完全一致）
.change-lang {
  position: fixed;
  right: 30px;
  top: 30px;
  z-index: 20;

  :deep {
    .change-lang {
      height: 36px;
      line-height: 36px;
      padding: 0 12px;
      background: #ffffff;
      border-radius: 18px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      color: #1f2937;
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover {
        background: #fff;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      }

      .icon {
        color: #409eff;
        margin-right: 6px;
      }
    }
  }
}

// 响应式适配（和登录页一致）
@media (max-width: 500px) {
  .login-container .login-card {
    width: 90%;
    padding: 30px 20px;
  }

  .login-form .avatar-uploader .avatar,
  .login-form .avatar-uploader .avatar-uploader-icon {
    width: 80px;
    height: 80px;
    line-height: 80px;
  }
}
</style>