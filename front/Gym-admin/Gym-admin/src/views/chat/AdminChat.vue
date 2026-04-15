<template>
  <div class="chat-container">
    <div class="layout">
      <div class="user-sidebar">
        <div class="side-title">咨询用户列表</div>
        <div v-if="userListLoading" class="loading-tip">加载中...</div>
        <div v-else-if="userList.length === 0" class="empty-tip">暂无咨询用户</div>
        <div
          v-for="u in userList"
          :key="u.userId"
          class="user-item"
          :class="{ active: currentUser?.userId === u.userId }"
          @click="selectUser(u)"
        >
          <div class="avatar">
            <img :src="u.userAvatar || defaultAvatar" alt="avatar" />
            <!-- 🔥 未读红点：大于0显示 -->
            <div class="unread-dot" v-if="u.unreadCount > 0">
              <span class="dot">{{ u.unreadCount }}</span>
            </div>
          </div>
          <div class="name">{{ u.userName }}</div>
        </div>
      </div>

      <div class="chat-box">
        <div class="chat-header" v-if="currentUser">
          与用户对话：{{ currentUser.userName }}
        </div>
        <div class="empty" v-else>
          {{ userListLoading ? '加载中...' : '请选择左侧用户开始对话' }}
        </div>

        <div class="msg-list" ref="msgRef" v-if="currentUser">
          <div
            v-for="msg in msgList"
            :key="msg.id"
            class="msg-item"
            :class="msg.senderType === 'admin' ? 'right' : 'left'"
          >
            <div class="avatar">
              <img
                :src="msg.senderType === 'admin' ? adminAvatar : currentUser.userAvatar || defaultAvatar"
                alt="avatar"
              />
            </div>
            <div class="bubble">{{ msg.content }}</div>
            <!-- 🔥 可选：消息时间展示（你之前需要的create_at） -->
            <div class="time">{{ formatTime(msg.created_at || msg.createdAt) }}</div>
          </div>
        </div>

        <div class="input-bar" v-if="currentUser">
          <el-input
            v-model="content"
            placeholder="回复用户..."
            @keyup.enter="send"
            :disabled="!socketConnected"
          />
          <el-button type="primary" @click="send" :disabled="!socketConnected">
            {{ socketConnected ? '发送' : '连接中' }}
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick, computed } from 'vue'
import { ElMessage } from 'element-plus'
import service from '@/utils/request'
import { useAccount } from '@/pinia/modules/account';

let ADMIN_ID = ref(null)
const defaultAvatar = ref('https://picsum.photos/id/1012/40/40')

const accountStore = useAccount()

const adminAvatar = computed(() => {
  return accountStore.userinfo?.avatar || defaultAvatar.value
})

const userInfo = computed(() => ({
  id: accountStore.userinfo?.id || 1,
  avatar: accountStore.userinfo?.avatar || defaultAvatar.value,
  username: accountStore.userinfo?.username || '未登录'
}))

const userList = ref([])
const currentUser = ref(null)
const msgList = ref([])
const content = ref('')
const msgRef = ref(null)
let socket = null

const userListLoading = ref(true)
const socketConnected = ref(false)

// 🔥 时间格式化（精确到秒，兼容create_at字段）
function formatTime(timeStr) {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    month: '2-digit', 
    day: '2-digit', 
    hour: '2-digit', 
    minute: '2-digit',
    second: '2-digit' // 🔥 新增：精确到秒
  }).replace(/\//g, '-')
}

// 连接WebSocket
function connect() {
  const wsUrl = `ws://localhost:9601/admin/chatAdmin/ws/chat/admin/${userInfo.value.id}`
  socket = new WebSocket(wsUrl)

  socket.onopen = () => {
    socketConnected.value = true
    ElMessage.success('已连接客服服务')
  }

  socket.onmessage = (e) => {
    try {
      const msg = JSON.parse(e.data)
      // 🔥 收到【用户发送的消息】：未读计数+1
      if (msg.senderType === 'user') {
        userList.value.forEach(user => {
          if (user.userId === msg.senderId) {
            // 不是当前选中的用户，才累加未读
            if (!currentUser.value || currentUser.value.userId !== user.userId) {
              user.unreadCount = (user.unreadCount || 0) + 1
            }
          }
        })
      }

      if (!currentUser.value) return
      const isCurrentMsg =
        (msg.senderType === 'user' && msg.senderId === currentUser.value.userId) ||
        (msg.senderType === 'admin' && msg.receiveUserId === currentUser.value.userId)

      if (isCurrentMsg) {
        msgList.value.push(msg)
        scrollBottom()
      }
    } catch (err) {
      console.error('消息解析失败', err)
    }
  }

  socket.onerror = () => {
    socketConnected.value = false
    ElMessage.error('客服服务连接失败')
  }

  socket.onclose = () => {
    socketConnected.value = false
  }
}

// 加载用户列表
async function loadUserList() {
  userListLoading.value = true
  try {
    const res = await service.get('/admin/chat/chatAdmin/admin/users')
    // 🔥 初始化：给每个用户添加未读计数=0
    userList.value = (res.data || []).map(item => ({
      ...item,
      unreadCount: 0
    }))
  } catch (err) {
    ElMessage.error('用户列表加载失败')
  } finally {
    userListLoading.value = false
  }
}

// 选择用户 → 加载历史 + 清空未读
async function selectUser(user) {
  msgList.value = []
  currentUser.value = user
  // 🔥 选中用户：清空未读红点
  user.unreadCount = 0

  try {
    const res = await service.get(`/admin/chat/chatAdmin/history/${user.userId}`)
    msgList.value = res.data || []
    scrollBottom()
  } catch (err) {
    ElMessage.error('聊天记录加载失败')
  }
}

// 发送消息
function send() {
  if (!socketConnected.value || !currentUser.value || !content.value.trim()) return

  const data = {
    senderType: 'admin',
    senderId: userInfo.value.id,
    userName: userInfo.value.username,
    userAvatar: userInfo.value.avatar,
    receiveUserId: currentUser.value.userId,
    content: content.value.trim()
  }

  socket.send(JSON.stringify(data))
  msgList.value.push(data)
  content.value = ''
  scrollBottom()
}

// 滚动到底
function scrollBottom() {
  nextTick(() => {
    if (msgRef.value) {
      msgRef.value.scrollTop = msgRef.value.scrollHeight
    }
  })
}

onMounted(() => {
  accountStore.getUserinfo()
  connect()
  loadUserList()
})

onBeforeUnmount(() => {
  if (socket) socket.close()
})
</script>

<style scoped>
.chat-container {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  padding: 0;
}
.layout {
  display: flex;
  height: calc(100vh - 200px);
  gap: 20px;
}
.user-sidebar {
  width: 260px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow-y: auto;
}
.side-title {
  padding: 12px 16px;
  font-weight: 500;
  background: #f7f8fa;
  border-bottom: 1px solid #eee;
  font-size: 14px;
}
.loading-tip, .empty-tip {
  padding: 20px;
  text-align: center;
  color: #999;
  font-size: 14px;
}
.user-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 16px;
  cursor: pointer;
  transition: background 0.2s;
  /* 🔥 红点定位父级 */
  position: relative;
}
.user-item:hover {
  background: #f5f7fa;
}
.user-item.active {
  background: #e8f3ff;
}
.avatar {
  position: relative; /* 🔥 红点定位 */
}
.avatar img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}
/* 🔥 未读红点样式 */
.unread-dot {
  position: absolute;
  top: -4px;
  right: -4px;
  z-index: 1;
}
.dot {
  display: inline-block;
  min-width: 18px;
  height: 18px;
  line-height: 18px;
  border-radius: 9px;
  background: #f56c6c;
  color: #fff;
  font-size: 11px;
  text-align: center;
  padding: 0 2px;
}
.name {
  font-size: 14px;
  color: #333;
}
.chat-box {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.chat-header {
  padding: 12px 16px;
  background: #f7f8fa;
  border-bottom: 1px solid #eee;
  font-weight: 500;
  font-size: 14px;
}
.empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 14px;
}
.msg-list {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.msg-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  flex-wrap: wrap; /* 🔥 时间换行 */
}
.msg-item.left {
  justify-content: flex-start;
}
.msg-item.right {
  flex-direction: row-reverse;
}

.bubble {
  max-width: 65%;
  padding: 12px 16px;
  border-radius: 12px;
  word-break: break-all;
  font-size: 16px;
  color: #fff;
}
.msg-item.left .bubble {
  background: #d8dad6;
}
.msg-item.right .bubble {
  background: #04ad1b;
}
/* 🔥 消息时间样式 */
.time {
  width: 100%;
  text-align: center;
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

/* ====================== 🔥 核心修改：放大输入框 ====================== */
.input-bar {
  display: flex;
  gap: 10px;
  /* 增加上下内边距，让输入栏更高 */
  padding: 16px;
  border-top: 1px solid #eee;
  background: #fafafa;
}
/* 深度修改 ElementPlus 输入框样式：放大高度、内边距 */
:deep(.el-input) {
  height: 52px;
}
:deep(.el-input__wrapper) {
  height: 100%;
  box-shadow: none;
  border: 1px solid #eee;
}
:deep(.el-input__inner) {
  height: 100%;
  font-size: 15px;
  padding: 0 12px;
}
/* 同步放大发送按钮，和输入框对齐 */
:deep(.el-button) {
  height: 52px;
  padding: 0 24px;
  font-size: 15px;
}
</style>