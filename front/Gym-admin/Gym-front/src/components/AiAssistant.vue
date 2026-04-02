<template>
  <div class="ai-assistant">
    <!-- 悬浮小球 -->
    <div class="float-btn" @click="showChat = !showChat">
      <span class="icon">AI</span>
    </div>

    <!-- 聊天窗口 -->
    <div class="chat-panel" v-show="showChat">
      <div class="chat-header">体育场馆智能助手</div>
      <div class="chat-body" ref="chatBody">
        <div v-for="(msg, idx) in messages" :key="idx" class="msg">
          <div v-if="msg.role === 'ai'" class="ai-msg">{{ msg.content }}</div>
          <div v-else class="user-msg">{{ msg.content }}</div>
        </div>
      </div>
      <div class="chat-footer">
        <el-input v-model="inputMsg" @keyup.enter="send" placeholder="提问..." />
        <el-button type="primary" @click="send">发送</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const showChat = ref(false)
const inputMsg = ref('')
const chatBody = ref(null)

const messages = ref([{
  role: 'ai',
  content: '你好！我是体育场馆AI客服，有什么可以帮你？'
}])

const send = async () => {
  if (!inputMsg.value.trim()) return ElMessage.warning('请输入内容')

  const msg = inputMsg.value
  messages.value.push({ role: 'user', content: msg })
  inputMsg.value = ''

  try {
    const { data } = await axios.post('http://localhost:9601/front/agent/chat', { message: msg })
    // ✅ 修复这里：你的后端返回 Result，所以取 data.data
    messages.value.push({ role: 'ai', content: data.data })
  } catch (e) {
    ElMessage.error('连接 AI 服务失败')
  }

  nextTick(() => {
    bodyRef.value.scrollTop = bodyRef.value.scrollHeight
  })
}
</script>

<style scoped>
.ai-assistant {
  position: fixed;
  right: 20px;
  bottom: 20px;
  z-index: 9999;
}

.float-btn {
  width: 60px;
  height: 60px;
  background: #409eff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: bold;
  cursor: pointer;
  box-shadow: 0 4px 8px #0002;
}

.chat-panel {
  position: absolute;
  right: 0;
  bottom: 70px;
  width: 350px;
  height: 500px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px #0002;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  padding: 14px;
  background: #409eff;
  color: #fff;
  font-weight: bold;
}

.chat-body {
  flex: 1;
  padding: 14px;
  overflow-y: auto;
  background: #f5f5f5;
}

.msg {
  margin-bottom: 10px;
}

.ai-msg {
  max-width: 70%;
  background: #fff;
  padding: 8px 12px;
  border-radius: 12px;
  float: left;
}

.user-msg {
  max-width: 70%;
  background: #409eff;
  color: #fff;
  padding: 8px 12px;
  border-radius: 12px;
  float: right;
}

.chat-footer {
  display: flex;
  padding: 10px;
  gap: 8px;
  background: #fff;
}
</style>