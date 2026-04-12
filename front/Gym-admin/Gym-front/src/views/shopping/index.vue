<template>
  <div class="shopping-page">
    <div class="container">
      <!-- 搜索和分类筛选 -->
      <div class="search-bar">
        <el-input
          v-model="searchKey"
          placeholder="搜索商品..."
          clearable
          class="search-input"
          @clear="loadGoods"
          @keyup.enter="loadGoods"
        >
          <template #append>
            <el-button @click="loadGoods">搜索</el-button>
          </template>
        </el-input>
        <div class="category-tabs">
          <el-button
            v-for="cat in categories"
            :key="cat.value"
            :type="selectedCategory === cat.value ? 'primary' : 'default'"
            @click="selectCategory(cat.value)"
          >
            {{ cat.label }}
          </el-button>
        </div>
      </div>

      <!-- 商品列表 -->
      <div class="goods-list">
        <el-row :gutter="20">
          <el-col :xs="12" :sm="8" :md="6" :lg="6" v-for="goods in goodsList" :key="goods.id">
            <el-card class="goods-card" shadow="hover" @click="goToDetail(goods.id)">
              <div class="goods-image">
                <img :src="goods.image" :alt="goods.goodsName" />
                <el-tag v-if="goods.status === 1" type="success" class="status-tag">上架</el-tag>
                <el-tag v-else type="danger" class="status-tag">下架</el-tag>
              </div>
              <div class="goods-info">
                <h3 class="goods-name">{{ goods.goodsName }}</h3>
                <p class="goods-category">{{ getCategoryText(goods.category) }}</p>
                <div class="goods-bottom">
                  <span class="price">¥{{ goods.price }}</span>
                  <el-button
                    type="primary"
                    size="small"
                    :disabled="goods.status !== 1 || goods.stock <= 0"
                    @click.stop="addToCart(goods)"
                  >
                    加入购物车
                  </el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 空状态 -->
        <el-empty v-if="goodsList.length === 0 && !loading" description="暂无商品" />
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadGoods"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useCart } from '@/stores/cart'
import { GetGoodsList } from '@/api/shopping'

const router = useRouter()
const cartStore = useCart()

const goodsList = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)
const searchKey = ref('')
const selectedCategory = ref('')

const categories = [
  { label: '全部', value: '' },
  { label: '饮品', value: 1 },
  { label: '零食', value: 2 },
  { label: '日用品', value: 3 }
]

const getCategoryText = (category) => {
  const map = { 1: '饮品', 2: '零食', 3: '日用品' }
  return map[category] || category
}

const selectCategory = (value) => {
  selectedCategory.value = value
  page.value = 1
  loadGoods()
}

const loadGoods = async () => {
  loading.value = true
  try {
    //获取所有商品
    const res = await GetGoodsList(page.value, pageSize.value, {
      category: selectedCategory.value,
      goodsName: searchKey.value
    })
    if (res.code === 200) {
      goodsList.value = res.data.list
      total.value = res.data.total
    }
    
    total.value = goodsList.value.length
  } catch (error) {
    console.error('加载商品失败:', error)
    ElMessage.error('加载商品失败')
  } finally {
    loading.value = false
  }
}

const goToDetail = (id) => {
  router.push(`/shopping/detail/${id}`)
}

const addToCart = async (goods) => {
  if (goods.status !== 1) {
    ElMessage.warning('该商品已下架')
    return
  }
  if (goods.stock <= 0) {
    ElMessage.warning('该商品库存不足')
    return
  }
  const success = await cartStore.addToCart(goods.id, 1)
  if (success) {
    ElMessage.success('已加入购物车')
  } else {
    ElMessage.error('加入购物车失败')
  }
}

onMounted(() => {
  loadGoods()
})
</script>

<style scoped lang="scss">
.shopping-page {
  min-height: calc(100vh - 140px);
  background: #f5f5f5;
  padding: 30px 0;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.search-bar {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

  .search-input {
    margin-bottom: 15px;
  }

  .category-tabs {
    display: flex;
    gap: 10px;
    flex-wrap: wrap;
  }
}

.goods-list {
  .goods-card {
    margin-bottom: 20px;
    cursor: pointer;

    &:hover {
      transform: translateY(-2px);
      transition: all 0.3s;
    }

    .goods-image {
      position: relative;
      height: 200px;
      overflow: hidden;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .status-tag {
        position: absolute;
        top: 10px;
        right: 10px;
      }
    }

    .goods-info {
      padding: 10px 0;

      .goods-name {
        font-size: 16px;
        margin: 0 0 8px 0;
        color: #333;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .goods-category {
        font-size: 12px;
        color: #999;
        margin: 0 0 10px 0;
      }

      .goods-bottom {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .price {
          font-size: 18px;
          font-weight: bold;
          color: #f7ba2a;
        }
      }
    }
  }
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}
</style>
