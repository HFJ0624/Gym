<template>
  <div class="venue-comment-container">
    <div class="comment-header">
      <h1>场馆评论</h1>
      <p class="subtitle">查看和发表场馆评论</p>
    </div>
    
    <!-- 场馆选择 -->
    <div class="venue-selector">
      <el-select 
        v-model="selectedVenueId" 
        placeholder="请选择场馆" 
        style="width: 300px"
        @change="onVenueChange"
      >
        <el-option 
          v-for="venue in venueList" 
          :key="venue.id" 
          :label="venue.venueName" 
          :value="venue.id" 
        />
      </el-select>
    </div>
    
    <!-- 发表评论 -->
    <div v-if="selectedVenueId" class="comment-form">
      <el-card>
        <template #header>
          <span>发表评论</span>
        </template>
        <el-form :model="commentForm" label-width="100px">
          <el-form-item label="评价">
            <el-rate v-model="commentForm.esteem" :max="5" />
          </el-form-item>
          <el-form-item label="评论内容">
            <el-input
              v-model="commentForm.content"
              type="textarea"
              :rows="4"
              placeholder="请输入评论内容"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitComment">提交评论</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
    
    <!-- 评论列表 -->
    <div v-if="selectedVenueId" class="comment-list">
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-avatar">
          <el-avatar :size="50" :src="comment.avatar" />
        </div>
        <div class="comment-content">
          <div class="comment-header-info">
            <span class="comment-author">{{ comment.username }}</span>
            <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
          </div>
          <div class="comment-text">{{ comment.content }}</div>
          <div class="comment-rating">
            <el-rate :model-value="comment.esteem" disabled show-score />
          </div>
        </div>
      </div>
      
      <!-- 无评论时显示 -->
      <div v-if="comments.length === 0 && !loading" class="empty-comment">
        <el-empty description="暂无评论" />
      </div>
      
      <!-- 加载中 -->
      <div v-if="loading" class="loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载中...</span>
      </div>
    </div>
    
    <!-- 分页 -->
    <div v-if="selectedVenueId && comments.length > 0" class="pagination">
      <el-pagination
        v-model:current-page="pageParams.page"
        v-model:page-size="pageParams.limit"
        :page-sizes="[5, 10, 20]"
        layout="total, sizes, prev, pager, next"
        :total="total"
        @size-change="fetchComments"
        @current-change="fetchComments"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElCard, ElForm, ElFormItem, ElInput, ElSelect, ElOption, ElButton, ElRate, ElAvatar, ElPagination, ElEmpty, ElIcon } from 'element-plus';
import { Loading } from '@element-plus/icons-vue';
import { GetVenueCommentListByPage, SubmitVenueComment } from '@/api/venueComment';
import { GetAllVenue } from '@/api/venues';
import { useAuth } from '@/stores/auth';

const auth = useAuth();

// 场馆列表
const venueList = ref([]);
const selectedVenueId = ref(null);

// 评论数据
const comments = ref([]);
const total = ref(0);
const loading = ref(false);

// 分页参数
const pageParams = ref({
  page: 1,
  limit: 10
});

// 评论表单
const commentForm = ref({
  venueId: null,
  content: '',
  esteem: 5
});

// 格式化时间
const formatTime = (time) => {
  return new Date(time).toLocaleString();
};

// 加载场馆列表
const loadVenueList = async () => {
  try {
    const { data, code } = await GetAllVenue({});
    if (code === 200) {
      venueList.value = data.allVenue || data.list || data || [];
    }
  } catch (error) {
    console.error('加载场馆列表失败:', error);
  }
};

// 场馆选择变化
const onVenueChange = () => {
  commentForm.value.venueId = selectedVenueId.value;
  pageParams.value.page = 1;
  fetchComments();
};

// 获取评论列表
const fetchComments = async () => {
  if (!selectedVenueId.value) return;
  
  loading.value = true;
  try {
    const { data, code, message } = await GetVenueCommentListByPage(
      selectedVenueId.value,
      pageParams.value.page,
      pageParams.value.limit
    );
    if (code === 200) {
      comments.value = data.list;
      total.value = data.total;
    } else {
      console.error('获取评论列表失败:', message);
    }
  } catch (error) {
    console.error('获取评论列表出错:', error);
  } finally {
    loading.value = false;
  }
};

// 提交评论
const submitComment = async () => {
  if (!commentForm.value.content.trim()) {
    ElMessage.warning('请输入评论内容');
    return;
  }
  
  if (!selectedVenueId.value) {
    ElMessage.warning('请选择场馆');
    return;
  }
  
  try {
    const { code, message } = await SubmitVenueComment({
      venueId: selectedVenueId.value,
      userId: auth.user?.id,
      content: commentForm.value.content,
      esteem: commentForm.value.esteem
    });
    
    if (code === 200) {
      ElMessage.success('评论提交成功');
      resetForm();
      fetchComments();
    } else {
      ElMessage.error(message || '评论提交失败');
    }
  } catch (error) {
    console.error('提交评论出错:', error);
    ElMessage.error('评论提交失败');
  }
};

// 重置表单
const resetForm = () => {
  commentForm.value = {
    venueId: selectedVenueId.value,
    content: '',
    esteem: 5
  };
};

// 页面加载时获取数据
onMounted(() => {
  loadVenueList();
});
</script>

<style scoped lang="scss">
.venue-comment-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.comment-header {
  text-align: center;
  margin-bottom: 30px;
}

.comment-header h1 {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.subtitle {
  font-size: 16px;
  color: #666;
}

.venue-selector {
  margin-bottom: 30px;
  text-align: center;
}

.comment-form {
  margin-bottom: 30px;
}

.comment-list {
  margin-bottom: 30px;
}

.comment-item {
  display: flex;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
}

.comment-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
  min-width: 0;
}

.comment-header-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-author {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-text {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
}

.comment-rating {
  display: flex;
  align-items: center;
}

.empty-comment,
.loading {
  text-align: center;
  padding: 60px 0;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  color: #909399;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .venue-comment-container {
    padding: 10px;
  }
  
  .comment-header h1 {
    font-size: 24px;
  }
  
  .comment-item {
    padding: 15px;
  }
}
</style>
