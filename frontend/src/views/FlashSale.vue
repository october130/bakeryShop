<template>
  <div class="flash-sale-page">
    <div class="page-header container">
      <h1 class="page-header__title">限时抢购</h1>
      <p class="page-header__desc">精选蛋糕，限时特价，手慢无！</p>
    </div>

    <div class="container">
      <div class="sale-list" v-loading="loading">
        <div class="sale-card" v-for="sale in sales" :key="sale.id">
          <div class="sale-card__image">
            <img :src="sale.cakeImage || defaultImage" :alt="sale.cakeName" @error="(e) => e.target.src = defaultImage" />
            <span class="sale-badge" v-if="sale.status === 1">抢购中</span>
            <span class="sale-badge sale-badge--upcoming" v-else-if="sale.status === 0">即将开始</span>
            <span class="sale-badge sale-badge--ended" v-else>已结束</span>
          </div>
          <div class="sale-card__info">
            <h3>{{ sale.cakeName }}</h3>
            <div class="sale-prices">
              <span class="flash-price">¥{{ (sale.flashPrice / 100).toFixed(0) }}</span>
              <span class="original-price">¥{{ (sale.originalPrice / 100).toFixed(0) }}</span>
            </div>
            <div class="sale-meta">
              <span>库存：{{ sale.stock }}</span>
              <span>{{ formatTime(sale.beginTime) }} - {{ formatTime(sale.endTime) }}</span>
            </div>
            <el-button
              type="danger"
              :disabled="sale.status !== 1 || sale.stock <= 0"
              :loading="seckilling === sale.id"
              @click="handleSeckill(sale)"
            >
              {{ sale.status === 0 ? '等待开始' : sale.status === 2 ? '已结束' : sale.stock <= 0 ? '已抢光' : '立即抢购' }}
            </el-button>
          </div>
        </div>
        <el-empty v-if="!loading && sales.length === 0" description="暂无抢购活动" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { flashSaleApi } from '@/api/modules'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const seckilling = ref(null)
const sales = ref([])
const defaultImage = 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400&h=400&fit=crop'

// 从测试数据加载（后端没有列表接口，用已知ID查询）
const SALE_IDS = [1, 2, 3]

const fetchSales = async () => {
  loading.value = true
  try {
    const results = await Promise.allSettled(
      SALE_IDS.map(id => flashSaleApi.detail(id))
    )
    sales.value = results
      .filter(r => r.status === 'fulfilled' && r.value)
      .map(r => r.value)
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const handleSeckill = async (sale) => {
  seckilling.value = sale.id
  try {
    const result = await flashSaleApi.seckill(sale.id)
    ElMessage.success(`抢购成功！订单号：${result}`)
    await fetchSales()
  } catch (e) {
    ElMessage.error(e.message || '抢购失败')
  } finally {
    seckilling.value = null
  }
}

const formatTime = (time) => {
  if (!time) return ''
  return time.replace('T', ' ').substring(0, 16)
}

onMounted(fetchSales)
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.flash-sale-page { padding-bottom: 60px; }

.sale-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 24px;
  margin-top: 24px;
}

.sale-card {
  background: $white;
  border-radius: $radius-lg;
  overflow: hidden;
  box-shadow: $shadow-sm;

  &__image {
    position: relative;
    height: 200px;
    background: $bg-warm;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  &__info {
    padding: 20px;

    h3 { font-size: 1.1rem; margin-bottom: 12px; }
  }
}

.sale-badge {
  position: absolute;
  top: 12px;
  left: 12px;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  background: #e74c3c;
  color: white;

  &--upcoming { background: #f39c12; }
  &--ended { background: #95a5a6; }
}

.sale-prices {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 12px;
}

.flash-price {
  font-size: 1.5rem;
  font-weight: 700;
  color: #e74c3c;
}

.original-price {
  font-size: 0.9rem;
  color: $text-muted;
  text-decoration: line-through;
}

.sale-meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.85rem;
  color: $text-light;
  margin-bottom: 16px;
}
</style>
