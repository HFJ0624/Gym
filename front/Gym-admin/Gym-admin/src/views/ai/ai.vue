<template>
  <div class="ai-agent-container">
    <!-- 主体布局：左侧会话 + 右侧聊天区 -->
    <div class="agent-main">
      <!-- 左侧会话栏 -->
      <div class="session-sidebar">
        <div class="session-header">
          <span>会话历史</span>
          <el-button type="text" icon="el-icon-delete" @click="clearSession">清空</el-button>
        </div>
        <div class="session-list">
          <div 
            v-for="(item, index) in sessionList" 
            :key="index"
            class="session-item"
            :class="{ active: activeSession === index }"
            @click="switchSession(index)"
          >
            <el-icon><ChatDotRound /></el-icon>
            <span class="session-title">{{ item.title }}</span>
          </div>
        </div>
      </div>

      <!-- 右侧聊天主区域 -->
      <div class="chat-main">
        <!-- 顶部标题 -->
        <div class="chat-header">
          <h3>后台管理智能助手</h3>
          <p>助力场馆系统运维、数据查询、问题排查</p>
        </div>

        <!-- 快捷功能卡片 -->
        <div class="quick-card">
          <div class="quick-item" @click="sendQuickMsg('查询今日场馆预约数据')">
            <el-icon><Calendar /></el-icon>
            <span>预约数据</span>
          </div>
          <div class="quick-item" @click="sendQuickMsg('统计今日订单营收')">
            <el-icon><Money /></el-icon>
            <span>订单统计</span>
          </div>
          <div class="quick-item" @click="sendQuickMsg('排查系统接口异常')">
            <el-icon><Tools /></el-icon>
            <span>故障排查</span>
          </div>
          <div class="quick-item" @click="sendQuickMsg('生成后台管理代码模板')">
            <el-icon><Document /></el-icon>
            <span>代码生成</span>
          </div>
        </div>

        <!-- 聊天消息区域 -->
        <div class="chat-message" ref="messageRef">
          <div 
            v-for="(msg, index) in messageList" 
            :key="index"
            class="message-item"
            :class="msg.role === 'user' ? 'user-msg' : 'ai-msg'"
          >
            <div class="msg-avatar">
              <el-icon v-if="msg.role === 'ai'"><Robot /></el-icon>
              <el-icon v-else><User /></el-icon>
            </div>
            <div class="msg-content">
              <p>{{ msg.content }}</p>
            </div>
          </div>
          <!-- 加载状态 -->
          <div v-if="loading" class="message-item ai-msg">
            <div class="msg-avatar"><el-icon><Robot /></el-icon></div>
            <div class="msg-content loading-text">思考中...</div>
          </div>
        </div>

        <!-- 输入框区域 -->
        <div class="chat-input">
          <el-input
            v-model="inputText"
            placeholder="输入问题，例如：查询所有场馆信息、修复跨域报错、生成预约报表"
            :rows="3"
            @keyup.enter="sendMessage"
          />
          <div class="input-btn">
            <el-button @click="clearChat">清空</el-button>
            <el-button type="primary" @click="sendMessage" :loading="loading">发送</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'

// 核心响应式数据
const messageRef = ref(null)
const inputText = ref('')
const loading = ref(false)
const activeSession = ref(0)

// 预设死数据 - 聊天记录
const messageList = ref([
  { role: 'ai', content: '你好！我是体育场馆后台智能助手，支持系统运维、数据查询、代码协助、故障排查，请问需要什么帮助？' },
  { role: 'user', content: '查询今日场馆预约数据' },
  { role: 'ai', content: '📊 今日场馆预约数据统计：\n\n🏀 篮球馆：45单（剩余2个场地）\n🏸 羽毛球馆：38单（已满场）\n🏓 乒乓球馆：25单（剩余3个场地）\n⚽ 足球场：20单（剩余1个场地）\n\n总计：128单，空闲场地：6个' }
])

// 预设死数据 - 会话历史
const sessionList = ref([
  { title: '系统运维咨询' },
  { title: '场馆数据查询' },
  { title: '代码问题排查' }
])

// 预设死数据 - AI回复库（本地模拟）
const aiReplyDatabase = {
  '预约': '📊 今日场馆预约数据统计：\n\n🏀 篮球馆：45单（剩余2个场地）\n🏸 羽毛球馆：38单（已满场）\n🏓 乒乓球馆：25单（剩余3个场地）\n⚽ 足球场：20单（剩余1个场地）\n\n总计：128单，空闲场地：6个',
  '订单': '💰 今日订单营收统计：\n\n📈 总营收：8,650元\n✅ 支付成功：98.2%\n🔄 退款订单：0单\n⏰ 待支付：3单\n\n支付渠道：微信65%，支付宝35%',
  '报错': '🔧 系统健康检测报告：\n\n✅ WebSocket连接正常\n✅ 数据库响应耗时12ms\n✅ 所有接口状态200\n✅ 服务器CPU使用率：28%\n\n未检测到异常，系统运行良好',
  '代码': '💻 后台管理CRUD代码模板已生成：\n\n```java\n// Controller层\n@PostMapping("/save")\npublic Result save(@RequestBody Venue venue) {\n    venueService.save(venue);\n    return Result.success();\n}\n```\n\n需要完整的Vue+SpringBoot代码请告诉我',
  'default': '我已理解你的需求，可提供后台管理全流程协助。你可以试试问我：\n• 场馆预约数据\n• 订单营收统计\n• 系统故障排查\n• 代码生成协助'
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messageRef.value) {
      messageRef.value.scrollTop = messageRef.value.scrollHeight
    }
  })
}

// 发送消息（纯本地模拟）
const sendMessage = () => {
  if (!inputText.value.trim()) return ElMessage.warning('请输入问题内容')
  const userMsg = inputText.value.trim()
  
  // 添加用户消息
  messageList.value.push({ role: 'user', content: userMsg })
  inputText.value = ''
  loading.value = true
  scrollToBottom()

  // 本地模拟AI回复（无接口）
  setTimeout(() => {
    let aiReply = aiReplyDatabase.default
    for (let key in aiReplyDatabase) {
      if (userMsg.includes(key)) {
        aiReply = aiReplyDatabase[key]
        break
      }
    }
    messageList.value.push({ role: 'ai', content: aiReply })
    loading.value = false
    scrollToBottom()
  }, 1000)
}

// 快捷消息发送
const sendQuickMsg = (text) => {
  inputText.value = text
  sendMessage()
}

// 清空聊天
const clearChat = () => {
  messageList.value = [{ role: 'ai', content: '聊天记录已清空，请问需要什么帮助？' }]
}

// 会话切换
const switchSession = (index) => {
  activeSession.value = index
  // 模拟不同会话切换不同内容
  if (index === 0) {
    messageList.value = [
      { role: 'ai', content: '欢迎咨询系统运维问题，我可以帮你查看日志、检测接口、排查报错。' }
    ]
  } else if (index === 1) {
    messageList.value = [
      { role: 'ai', content: '数据查询助手已就绪，请告诉我你想查询的时间范围或场馆类型。' }
    ]
  } else {
    messageList.value = [
      { role: 'ai', content: '代码协助模式已开启，支持生成CRUD、WebSocket、订单支付等代码模板。' }
    ]
  }
}

// 清空会话历史
const clearSession = () => {
  sessionList.value = [{ title: '新建会话' }]
  activeSession.value = 0
  clearChat()
  ElMessage.success('会话历史已清空')
}

// 初始化滚动
scrollToBottom()
</script>

<style scoped>
/* 整体容器 - 匹配后台原生样式 */
.ai-agent-container {
  width: 100%;
  height: calc(100vh - 60px);
  background-color: #f5f5f5;
  padding: 0;
  box-sizing: border-box;
}
.agent-main {
  display: flex;
  width: 100%;
  height: 100%;
  gap: 1px;
  background-color: #e5e7eb;
}

/* 左侧会话栏 */
.session-sidebar {
  width: 220px;
  background-color: #fff;
  display: flex;
  flex-direction: column;
}
.session-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #eee;
  font-weight: 500;
}
.session-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px;
}
.session-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 12px;
  border-radius: 6px;
  cursor: pointer;
  margin-bottom: 4px;
  transition: all 0.2s;
}
.session-item:hover {
  background-color: #f0f2f5;
}
.session-item.active {
  background-color: #409eff;
  color: #fff;
}
.session-title {
  font-size: 13px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 右侧聊天主区域 */
.chat-main {
  flex: 1;
  background-color: #fff;
  display: flex;
  flex-direction: column;
  padding: 20px;
  box-sizing: border-box;
}
.chat-header {
  margin-bottom: 16px;
}
.chat-header h3 {
  margin: 0;
  font-size: 18px;
  color: #1f2937;
}
.chat-header p {
  margin: 4px 0 0;
  font-size: 13px;
  color: #6b7280;
}

/* 快捷功能卡片 */
.quick-card {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}
.quick-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  font-size: 13px;
}
.quick-item:hover {
  border-color: #409eff;
  color: #409eff;
  transform: translateY(-2px);
}

/* 聊天消息区域 */
.chat-message {
  flex: 1;
  overflow-y: auto;
  padding-right: 8px;
  margin-bottom: 16px;
}
.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}
.user-msg {
  flex-direction: row-reverse;
}
.msg-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.user-msg .msg-avatar {
  background-color: #409eff;
  color: #fff;
}
.msg-content {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 12px;
  line-height: 1.6;
  font-size: 14px;
  white-space: pre-wrap;
}
.ai-msg .msg-content {
  background-color: #f8fafc;
  border: 1px solid #e2e8f0;
}
.user-msg .msg-content {
  background-color: #409eff;
  color: #fff;
}
.loading-text {
  color: #9ca3af;
}

/* 输入框区域 */
.chat-input {
  border-top: 1px solid #eee;
  padding-top: 16px;
}
.input-btn {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 12px;
}
</style>