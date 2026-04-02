<template>
  <div class="notice-detail-container">
    <div class="notice-detail">
      <!-- 公告内容 -->
      <div class="notice-content">
        <h1 class="notice-title">{{ notice.title }}</h1>
        <div class="notice-meta">
          <span class="notice-type" :class="getTypeClass(notice.noticeType)">
            {{ getTypeText(notice.noticeType) }}
          </span>
          <span class="notice-time">{{ formatTime(notice.createTime) }}</span>
        </div>
        <div class="notice-body">
          {{ notice.content }}
        </div>
        <div class="notice-actions">
          <el-button type="primary" @click="goBack">返回列表</el-button>
        </div>
      </div>
      
      <!-- 评论区 -->
      <div class="comment-section">
        <h2 class="comment-title">评论 ({{ comments.length }})</h2>
        
        <!-- 评论输入框 -->
        <div class="comment-input">
          <div class="comment-rating-input">
            <span class="rating-label">评分：</span>
            <el-rate v-model="commentEsteem" :max="5" show-text>
              <template #text>
                <span style="margin-left: 10px; color: #ff9900; font-weight: 500;">{{ getEsteemText(commentEsteem) }}</span>
              </template>
            </el-rate>
          </div>
          <el-input
            v-model="commentContent"
            type="textarea"
            :rows="3"
            placeholder="写下你的评论..."
          />
          <div class="comment-actions">
            <el-button type="primary" @click="submitComment" :loading="submitting">
              发表评论
            </el-button>
          </div>
        </div>
        
        <!-- 评论列表 -->
        <div class="comment-list">
          <div v-for="comment in comments" :key="comment.id" class="comment-item">
            <div class="comment-avatar">
              <img :src="comment.avatar" alt="用户头像" />
            </div>
            <div class="comment-main">
              <div class="comment-header">
                <span class="comment-author">{{ comment.username }}</span>
                <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
              </div>
              <div class="comment-rating">
                <el-rate v-model="comment.esteem" disabled>
                  <template #text>
                    <span style="margin-left: 10px; color: #ff9900; font-weight: 500;">{{ getEsteemText(comment.esteem) }}</span>
                  </template>
                </el-rate>
              </div>
              <div class="comment-body">
                {{ comment.content }}
              </div>
            </div>
          </div>
          
          <!-- 无评论时显示 -->
          <div v-if="comments.length === 0" class="empty-comments">
            <el-empty description="暂无评论" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElEmpty, ElButton, ElInput, ElMessage, ElRate } from 'element-plus';
import { GetNoticeDetail, SubmitComment } from '@/api/notice';
import { useAuth } from '@/stores/auth';

const route = useRoute();
const router = useRouter();
const authStore = useAuth();

// 公告数据
const notice = ref({});

// 评论数据
const comments = ref([]);

// 评论输入
const commentContent = ref('');
const commentEsteem = ref(5); // 默认给5星好评
const submitting = ref(false);

// 格式化时间
const formatTime = (time) => {
  return new Date(time).toLocaleString();
};

// 获取公告类型文本
const getTypeText = (type) => {
  switch(type) {
    case 1: return '系统公告';
    case 2: return '活动通知';
    case 3: return '新闻';
    default: return '其他';
  }
};

// 获取公告类型样式类
const getTypeClass = (type) => {
  switch(type) {
    case 1: return 'type-system';
    case 2: return 'type-activity';
    case 3: return 'type-news';
    default: return '';
  }
};

// 获取评分文字
const getEsteemText = (value) => {
  switch(value) {
    case 5: return '好评';
    case 4: return '良好';
    case 3: return '中等';
    case 2: return '一般';
    case 1: return '差';
    default: return '';
  }
};

//表单数据模型
const defaultForm = {
    noticeId: route.params.id,
    userId: authStore.user.id,
    content: commentContent,
    esteem: commentEsteem
}
const queryDto = ref(defaultForm)   // 使用ref包裹该对象，使用reactive不方便进行重置

const submitComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容');
    return;
  }
  
  if (!authStore.user?.id) {
    ElMessage.warning('请先登录');
    return;
  }
  
  submitting.value = true;
  try {
    // 调用后端API提交评论
    const { code, message, data } = await SubmitComment(queryDto.value);
    
    if (code === 200) {
      commentContent.value = '';
      commentEsteem.value = 5; // 重置为默认5星
      ElMessage.success('评论发布成功');
      // 重新获取最新评论
      getNoticeDetail();
    } else {
      ElMessage.error(message || '评论发布失败');
    }
  } catch (error) {
    console.error('提交评论出错:', error);
    ElMessage.error('评论发布失败，请稍后重试');
  } finally {
    submitting.value = false;
  }
};
// 返回列表
const goBack = () => {
  router.push('/notice');
};

// 获取公告详情
const getNoticeDetail = async () => {
  const id = route.params.id;
  try {
    const { data, code, message } = await GetNoticeDetail(id);
    if (code === 200) {
      notice.value = data.notice || {};
      comments.value = data.allComments || [];
    } else {
      console.error('获取公告详情失败:', message);
      ElMessage.error('获取公告详情失败');
    }
  } catch (error) {
    console.error('获取公告详情出错:', error);
    ElMessage.error('获取公告详情出错');
  }
};

// 页面加载时获取数据
onMounted(() => {
  getNoticeDetail();
});
</script>

<style scoped>
.notice-detail-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.notice-detail {
  background: #fff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.notice-title {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
  line-height: 1.4;
}

.notice-meta {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.notice-type {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  margin-right: 15px;
}

.type-system {
  background: #ecf5ff;
  color: #409eff;
}

.type-activity {
  background: #f0f9eb;
  color: #67c23a;
}

.type-news {
  background: #fdf6ec;
  color: #e6a23c;
}

.notice-time {
  font-size: 14px;
  color: #999;
}

.notice-body {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  margin-bottom: 30px;
  white-space: pre-wrap;
}

.notice-actions {
  margin-bottom: 40px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

/* 评论区样式 */
.comment-section {
  margin-top: 40px;
}

.comment-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 20px;
}

.comment-input {
  background: #f9f9f9;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 30px;
}

.comment-rating-input {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.rating-label {
  font-size: 14px;
  color: #606266;
  margin-right: 10px;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 15px;
}

.comment-list {
  margin-top: 20px;
}

.comment-item {
  display: flex;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.comment-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  overflow: hidden;
  margin-right: 15px;
  flex-shrink: 0;
}

.comment-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.comment-main {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-author {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-rating {
  margin: 8px 0;
}

.comment-body {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
}

.empty-comments {
  text-align: center;
  padding: 40px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .notice-detail-container {
    padding: 10px;
  }
  
  .notice-detail {
    padding: 20px;
  }
  
  .notice-title {
    font-size: 20px;
  }
  
  .notice-body {
    font-size: 14px;
  }
  
  .comment-input {
    padding: 15px;
  }
}
</style>
