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

// ======================
// 1. 修复 WebSocket 接收逻辑：正确按用户ID隔离消息
// ======================
function connect() {
  const wsUrl = `ws://localhost:9601/admin/chatAdmin/ws/chat/admin/${ADMIN_ID}`
  socket = new WebSocket(wsUrl)

  socket.onopen = () => {
    socketConnected.value = true
    ElMessage.success('已连接客服服务')
  }

  socket.onmessage = (e) => {
    try {
      let msg = JSON.parse(e.data)
      // 兼容后端缺失字段，强制补全
      msg = {
        senderType: msg.senderType || 'user',
        content: msg.content || msg,
        senderId: msg.senderId,
        receiveUserId: msg.receiveUserId, // 后端必须返回接收者ID
        ...msg
      }

      // 🔥 核心修复：正确判断消息是否属于当前选中用户
      // 规则：消息的接收者是当前用户，或者消息的发送者是当前用户
      const isCurrentUserMsg = 
        currentUser.value && 
        (msg.senderId === currentUser.value.userId || msg.receiveUserId === currentUser.value.userId)

      if (isCurrentUserMsg) {
        // 强制保证msgList是数组，再push
        if (!Array.isArray(msgList.value)) msgList.value = []
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

// ======================
// 2. 加载用户列表（无改动，仅保留）
// ======================
async function loadUserList() {
  userListLoading.value = true
  try {
    const res = await service.get('/admin/chat/chatAdmin/admin/users')
    userList.value = res.data || []
  } catch (err) {
    ElMessage.error('用户列表加载失败')
    console.error(err)
  } finally {
    userListLoading.value = false
  }
}

// ======================
// 3. 修复选择用户：强制重置数组，彻底隔离聊天记录
// ======================
async function selectUser(user) {
  // 先清空当前聊天，防止旧数据污染
  msgList.value = []
  currentUser.value = user

  try {
    const res = await service.get(`/admin/chat/chatAdmin/history/${user.userId}`)
    // 🔥 强制保证返回的是数组，彻底解决非数组问题
    msgList.value = Array.isArray(res.data) ? res.data : []
    scrollBottom()
  } catch (err) {
    msgList.value = [] // 报错也清空，避免串号
    ElMessage.error('聊天记录加载失败')
    console.error(err)
  }
}

// ======================
// 4. 发送消息（修复：添加receiveUserId，确保后端能正确路由）
// ======================
function send() {
  if (!socketConnected.value) {
    ElMessage.warning('未连接，无法发送')
    return
  }
  if (!currentUser.value || !content.value.trim()) {
    ElMessage.warning('请输入内容')
    return
  }

  // 强制保证msgList是数组
  if (!Array.isArray(msgList.value)) msgList.value = []

  const data = {
    senderType: 'admin',
    senderId: ADMIN_ID,
    receiveUserId: currentUser.value.userId, // 🔥 必须带接收者ID，后端用来路由
    userName: '客服',
    userAvatar: adminAvatar,
    content: content.value.trim(),
    conversation_id: currentUser.value.userId
  }

  socket.send(JSON.stringify(data))
  msgList.value.push(data)
  content.value = ''
  scrollBottom()
}

// ======================
// 5. 滚动到底部（无改动，仅保留）
// ======================
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
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
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
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
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
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.msg-item {
  display: flex;
  gap: 12px;
}
.msg-item.left {
  align-self: flex-start;
}
.msg-item.right {
  align-self: flex-end;
  flex-direction: row-reverse;
}
.msg-item .avatar {
  flex-shrink: 0;
}
.msg-item .bubble {
  max-width: 70%;
  padding: 8px 12px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.5;
}
.msg-item.left .bubble {
  background: #f5f7fa;
  color: #333;
  border-bottom-left-radius: 0;
}
.msg-item.right .bubble {
  background: #409eff;
  color: #fff;
  border-bottom-right-radius: 0;
}
.input-bar {
  display: flex;
  gap: 12px;
  padding: 12px 16px;
  border-top: 1px solid #eee;
}
.input-bar .el-input {
  flex: 1;
}
</style>