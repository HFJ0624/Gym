<template>
  <div class="chat-page-wrapper">
    <div class="chat-container">
      <div class="chat-header">
        <span>场馆在线客服</span>
        <span v-if="socketConnected" class="status-badge">✅ 客服已连接</span>
      </div>

      <div class="msg-list" ref="msgRef">
        <!-- 1. 增加时间渲染 -->
        <div
          v-for="(msg, index) in msgList"
          :key="index"
          class="msg-item"
          :class="msg.senderType === 'user' ? 'right' : 'left'"
        >
          <div class="avatar">
            <img :src="msg.senderType === 'user' ? userInfo.avatar : (msg.userAvatar || adminInfo.avatar)" alt="头像">
          </div>
          <div class="bubble">{{ msg.content }}</div>
          <!-- 2. 时间气泡 -->
          <div class="time">{{ formatTime(msg.createdAt || msg.created_at) }}</div>
        </div>
      </div>

      <div class="input-bar">
        <el-input v-model="content" placeholder="请输入咨询内容..." @keyup.enter="send" />
        <el-button type="primary" @click="send">发送</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuth } from '@/stores/auth'

const authStore = useAuth()

// 使用 computed 响应式获取用户信息
const userInfo = computed(() => ({
  id: authStore.user?.id || 1,
  avatar: authStore.user?.avatar || 'https://picsum.photos/id/1012/40/40',
  username: authStore.user?.username || '用户'
}))

// 客服信息
const adminInfo = ref({
  id: 1,
  avatar: 'https://picsum.photos/id/1005/40/40',
  username: '客服'
})

// 加载客服信息
async function loadAdminInfo() {
  try {
    const res = await fetch(`http://localhost:9601/admin/chat/chatAdmin/admin/info`)
    const data = await res.data()
    if (data) {
      adminInfo.value = {
        id: data.id || 1,
        avatar: data.avatar || 'https://picsum.photos/id/1005/40/40',
        username: data.username || '客服'
      }
    }
  } catch (e) {
    console.log('加载客服信息失败，使用默认值')
  }
}

// ✅ 确保永远是数组
const msgList = ref([])
const content = ref('')
const msgRef = ref(null)
let socket = null
const socketConnected = ref(false)

// 3. 新增：时间格式化函数
function formatTime(timeStr) {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  // 格式化为：月-日 时:分
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  }).replace(/\//g, '-')
}

function connect() {
  const wsUrl = `ws://localhost:9601/admin/chatAdmin/ws/chat/user/${userInfo.value.id}`
  socket = new WebSocket(wsUrl)

  socket.onopen = () => {
    socketConnected.value = true
    ElMessage.success('客服已连接')
  }

  // 接收消息（修复格式）
  socket.onmessage = (e) => {
    try {
      const msg = JSON.parse(e.data)
      const newMsg = {
        senderType: msg.senderType || 'admin',
        content: msg.content || msg,
        createdAt: msg.createdAt || msg.created_at, // 4. 确保时间字段被正确接收
        ...msg
      }
      // ✅ 强制保证是数组再 push
      if (!Array.isArray(msgList.value)) msgList.value = []
      msgList.value.push(newMsg)
      scrollBottom()
    } catch (err) {
      console.log('消息解析失败', err)
    }
  }

  socket.onerror = () => {
    socketConnected.value = false
    ElMessage.error('连接失败')
  }

  socket.onclose = () => {
    socketConnected.value = false
  }
}

// 加载历史（最关键：强制保证是数组）
async function loadHistory() {
  try {
    const res = await fetch(`http://localhost:9601/admin/chat/chatAdmin/history/${userInfo.value.id}`)
    const data = await res.json()

    // ✅ 核心修复：接口返回不是数组就强制给 []
    msgList.value = Array.isArray(data) ? data : []
    scrollBottom()
  } catch (e) {
    // ✅ 报错也重置为空数组
    msgList.value = []
    console.log('暂无历史消息')
  }
}

// 发送消息
function send() {
  if (!content.value) return ElMessage.warning('请输入内容')
  if (!socket || socket.readyState !== WebSocket.OPEN) return ElMessage.error('未连接客服')

  // ✅ 发送前再次确保 msgList 是数组（终极保险）
  if (!Array.isArray(msgList.value)) {
    msgList.value = []
  }

  // 5. 发送时带上当前本地时间（如果后端不自动生成）
  const sendTime = new Date().toISOString()
  const data = {
    senderType: 'user',
    senderId: userInfo.value.id,
    userName: userInfo.value.username,
    userAvatar: userInfo.value.avatar,
    content: content.value,
    conversationId: userInfo.value.id,
    receiveUserId: 1,
    createdAt: sendTime // 可选，让前端先显示，等后端返回后更新
  }

  socket.send(JSON.stringify(data))
  msgList.value.push(data)
  content.value = ''
  scrollBottom()
}

function scrollBottom() {
  nextTick(() => {
    if (msgRef.value) msgRef.value.scrollTop = msgRef.value.scrollHeight
  })
}

onMounted(() => {
  loadAdminInfo()
  connect()
  loadHistory()
})

onBeforeUnmount(() => {
  if (socket) socket.close()
})
</script>

<style scoped>
/* 核心：100% 适配你的 FrontLayout 布局，不挤掉导航栏 */
.chat-page-wrapper {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 18px;
  box-sizing: border-box;
}
.chat-container {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  height: calc(100vh - 64px - 88px);
  overflow: hidden;
}
.chat-header {
  padding: 12px 16px;
  background: #f7f8fa;
  font-weight: 500;
  border-bottom: 1px solid #eee;
  font-size: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.status-badge {
  font-size: 12px;
  color: #67c23a;
  background: #f0f9eb;
  padding: 4px 8px;
  border-radius: 4px;
}
.msg-list {
  flex: 1;
  padding: 16px 20px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.msg-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  flex-wrap: wrap;
}
.msg-item.left {
  justify-content: flex-start;
}
.msg-item.right {
  flex-direction: row-reverse;
}
.avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}
.bubble {
  max-width: 65%;
  padding: 10px 14px;
  border-radius: 12px;
  background: #f5f5f5;
  word-break: break-all;
  font-size: 16px;
  line-height: 1.5;
}
.right .bubble {
  background: #04ad1b;
  color: #fff;
}
.time {
  width: 100%;
  text-align: center;
  font-size: 16px;
  color: #999;
  margin-top: 4px;
}

/* 🔥 放大输入框区域 核心修改 */
.input-bar {
  display: flex;
  gap: 10px;
  /* 增大上下内边距，更舒展 */
  padding: 18px;
  border-top: 1px solid #eee;
  background: #fafafa;
}
/* 放大 ElementPlus 输入框 */
:deep(.el-input) {
  height: 48px;
}
:deep(.el-input__wrapper) {
  height: 100%;
}
:deep(.el-input__inner) {
  font-size: 15px;
}
/* 放大发送按钮，和输入框对齐 */
:deep(.el-button) {
  height: 48px;
  padding: 0 24px;
  font-size: 15px;
}
</style>