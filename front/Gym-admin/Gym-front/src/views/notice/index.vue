<template>
  <div class="notice-container">
    <div class="notice-header">
      <h1>体育场馆公告</h1>
      <p class="subtitle">最新公告和活动信息</p>
    </div>
    
    <div class="notice-list">
      <div v-for="notice in notices" :key="notice.id" class="notice-item" @click="goToDetail(notice.id)">
        <div class="notice-content">
          <h3 class="notice-title">{{ notice.title }}</h3>
          <p class="notice-text">{{ notice.content }}</p>
          <div class="notice-meta">
            <span class="notice-type" :class="getTypeClass(notice.noticeType)">
              {{ getTypeText(notice.noticeType) }}
            </span>
            <span class="notice-time">{{ formatTime(notice.createTime) }}</span>
          </div>
        </div>
      </div>
      
      <!-- 无公告时显示 -->
      <div v-if="notices.length === 0" class="empty-notice">
        <el-empty description="暂无公告" />
      </div>
    </div>
    
    <!-- 分页 -->
    <!-- <div v-if="notices.length > 0" class="pagination">
      <el-pagination
        v-model:current-page="pageParams.page"
        v-model:page-size="pageParams.limit"
        :page-sizes="[5, 10, 20]"
        layout="total, sizes, prev, pager, next"
        :total="total"
        @size-change="fetchData"
        @current-change="fetchData"
      />
    </div> -->
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElEmpty, ElPagination } from 'element-plus';
import { GetAllNotice } from '@/api/notice';

const router = useRouter();

// 公告列表数据
const notices = ref([]);
const total = ref(0);
const pageParams = ref({
  page: 1,
  limit: 10
});

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

// 获取公告列表
const fetchData = async () => {
  try {
    const { data, code, message } = await GetAllNotice();
    if (code === 200) {
      notices.value = data.allNotices || data.list || data || [];
      total.value = data.total || notices.value.length;
    } else {
      console.error('获取公告列表失败:', message);
    }
  } catch (error) {
    console.error('获取公告列表出错:', error);
  }
};

// 跳转到详情页
const goToDetail = (id) => {
  router.push(`/notice/${id}`);
};

// 页面加载时获取数据
onMounted(() => {
  fetchData();
});
</script>

<style scoped>
.notice-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.notice-header {
  text-align: center;
  margin-bottom: 40px;
  padding-top: 20px;
}

.notice-header h1 {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.subtitle {
  font-size: 16px;
  color: #666;
}

.notice-list {
  margin-bottom: 30px;
}

.notice-item {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.notice-item:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.notice-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.notice-text {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.notice-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.notice-type {
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
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

.empty-notice {
  text-align: center;
  padding: 60px 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .notice-container {
    padding: 10px;
  }
  
  .notice-header h1 {
    font-size: 24px;
  }
  
  .notice-item {
    padding: 15px;
  }
  
  .notice-title {
    font-size: 16px;
  }
}
</style>