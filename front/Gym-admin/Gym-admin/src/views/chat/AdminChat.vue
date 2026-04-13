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
            <img :src="u.userAvatar || defaultAvatar" alt="头像" />
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
                alt="头像"
              />
            </div>
            <div class="bubble">{{ msg.content }}</div>
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
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import service from '@/utils/request'

const ADMIN_ID = 1
const defaultAvatar = 'https://picsum.photos/id/1012/40/40'
const adminAvatar = 'https://picsum.photos/id/1005/40/40'

const userList = ref([])
const currentUser = ref(null)
const msgList = ref([])
const content = ref('')
const msgRef = ref(null)
let socket = null

const userListLoading = ref(true)
const socketConnected = ref(false)

// 连接WebSocket
function connect() {
  const wsUrl = `ws://localhost:9601/admin/chatAdmin/ws/chat/admin/${ADMIN_ID}`
  socket = new WebSocket(wsUrl)

  socket.onopen = () => {
    socketConnected.value = true
    ElMessage.success('已连接客服服务')
  }

  socket.onmessage = (e) => {
    try {
      const msg = JSON.parse(e.data)
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
    userList.value = res.data || []
  } catch (err) {
    ElMessage.error('用户列表加载失败')
  } finally {
    userListLoading.value = false
  }
}

// 选择用户 → 加载历史
async function selectUser(user) {
  msgList.value = []
  currentUser.value = user

  try {
    const res = await service.get(`/admin/chat/chatAdmin/history/${user.userId}`)
    msgList.value = res.data || []
    scrollBottom()
  } catch (err) {
    ElMessage.error('聊天记录加载失败')
  }
}

// 发送消息（绝对不报错！）
function send() {
  if (!socketConnected.value || !currentUser.value || !content.value.trim()) return

  const data = {
    senderType: 'admin',
    senderId: ADMIN_ID,
    receiveUserId: currentUser.value.userId, // 🔥 必须传！
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
}
.user-item:hover {
  background: #f5f7fa;
}
.user-item.active {
  background: #e8f3ff;
}
.avatar img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
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
}
.msg-item.left {
  justify-content: flex-start;
}
.msg-item.right {
  flex-direction: row-reverse;
}
.bubble {
  max-width: 65%;
  padding: 8px 12px;
  border-radius: 12px;
  background: #f5f5f5;
  word-break: break-all;
  font-size: 14px;
}
.right .bubble {
  background: #409eff;
  color: #fff;
}
.input-bar {
  display: flex;
  gap: 10px;
  padding: 12px 16px;
  border-top: 1px solid #eee;
}
</style>