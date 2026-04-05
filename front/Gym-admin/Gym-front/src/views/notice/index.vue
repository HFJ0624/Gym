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
  max-width: 900px;
  margin: 0 auto;
  padding: 0 18px;
}

.notice-header {
  text-align: center;
  margin-bottom: 48px;
  padding-top: 32px;
}

.notice-header h1 {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 12px;
  letter-spacing: -0.5px;
}

.subtitle {
  font-size: 16px;
  color: #666;
}

.notice-list {
  margin-bottom: 32px;
}

.notice-item {
  background: #fff;
  border: 1px solid #e5e5e5;
  padding: 28px;
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.25s ease;
}

.notice-item:hover {
  border-color: #1a1a1a;
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.08);
}

.notice-title {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 12px;
}

.notice-text {
  font-size: 15px;
  color: #666;
  line-height: 1.7;
  margin-bottom: 16px;
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
  font-size: 13px;
  color: #888;
}

.notice-type {
  padding: 4px 12px;
  font-size: 13px;
  font-weight: 600;
  border: 1px solid #1a1a1a;
  color: #1a1a1a;
}

.type-system {
  background: #f5f5f5;
  color: #1a1a1a;
  border-color: #1a1a1a;
}

.type-activity {
  background: #1a1a1a;
  color: #fff;
  border-color: #1a1a1a;
}

.type-news {
  background: #fff;
  color: #1a1a1a;
  border-color: #1a1a1a;
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
    padding: 0 18px;
  }
  
  .notice-header h1 {
    font-size: 26px;
  }
  
  .notice-item {
    padding: 20px;
  }
  
  .notice-title {
    font-size: 18px;
  }
}
</style>