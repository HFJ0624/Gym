<template>
  <!-- 直接嵌入主内容区，完全适配你的 FrontLayout 布局 -->
  <div class="chat-page-wrapper">
    <div class="chat-container">
      <div class="chat-header">
        <span>场馆在线客服</span>
        <span v-if="socketConnected" class="status-badge">✅ 客服已连接</span>
      </div>

      <div class="msg-list" ref="msgRef">
        <div
          v-for="(msg, index) in msgList"
          :key="index"
          class="msg-item"
          :class="msg.senderType === 'user' ? 'right' : 'left'"
        >
          <div class="avatar">
            <img :src="msg.senderType === 'user' ? userInfo.avatar : 'https://picsum.photos/id/1005/40/40'" alt="头像">
          </div>
          <div class="bubble">{{ msg.content }}</div>
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
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuth } from '@/stores/auth'

const authStore = useAuth()
const userInfo = ref({
  id: authStore.user.id || 1,
  avatar: authStore.user.avatar || 'https://picsum.photos/id/1012/40/40',
  userName: authStore.user.username
})

// ✅ 确保永远是数组
const msgList = ref([])
const content = ref('')
const msgRef = ref(null)
let socket = null
const socketConnected = ref(false)

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

  const data = {
    senderType: 'user',
    senderId: userInfo.value.id,
    userName: userInfo.value.userName,
    userAvatar: userInfo.value.avatar,
    content: content.value,
    conversationId: userInfo.value.id,
    receiveUserId: 1
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
  padding: 0 18px; /* 和你 header 的 inner 内边距完全一致 */
  box-sizing: border-box;
}
.chat-container {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  /* 关键高度：精准适配你的 header(64px) + footer(24px*2) + 上下间距 */
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
}
.msg-item.left {
  justify-content: flex-start;
}
.msg-item.right {
  flex-direction: row-reverse;
}
.avatar img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}
.bubble {
  max-width: 65%;
  padding: 8px 12px;
  border-radius: 12px;
  background: #f5f5f5;
  word-break: break-all;
  font-size: 14px;
  line-height: 1.5;
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
  background: #fafafa;
}
</style>