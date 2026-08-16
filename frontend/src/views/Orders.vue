<template>
  <div class="orders-page">
    <div class="page-header container">
      <h1 class="page-header__title">我的订单</h1>
    </div>

    <div class="container">
      <!-- 订单筛选 -->
      <div class="order-tabs">
        <button
          v-for="tab in tabs"
          :key="tab.value"
          :class="['tab-btn', { active: activeTab === tab.value }]"
          @click="activeTab = tab.value"
        >
          {{ tab.label }}
        </button>
      </div>

      <!-- 订单列表 -->
      <div class="order-list" v-loading="loading">
        <div class="order-card" v-for="order in filteredOrders" :key="order.id">
          <div class="order-header">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <span :class="['order-status', `status-${order.status}`]">
              {{ statusMap[order.status] }}
            </span>
          </div>

          <div class="order-body" @click="$router.push(`/order/${order.id}`)">
            <div class="order-items">
              <div class="item-placeholder">🎂</div>
              <div class="item-info">
                <h4>蛋糕商品</h4>
                <p>{{ order.totalPrice ? `${order.totalPrice}元` : '' }}</p>
              </div>
            </div>
            <div class="order-info">
              <p class="order-time">{{ order.createTime }}</p>
              <p class="order-price">¥{{ order.totalPrice }}</p>
            </div>
          </div>

          <div class="order-footer">
            <template v-if="order.status === 0">
              <el-button @click="cancelOrder(order.id)">取消订单</el-button>
              <el-button type="primary" @click="payOrder(order.id)">去支付</el-button>
            </template>
            <template v-else-if="order.status === 4">
              <el-button type="primary" @click="$router.push(`/review/${order.id}`)">
                去评价
              </el-button>
            </template>
            <el-button text @click="$router.push(`/order/${order.id}`)">查看详情</el-button>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && filteredOrders.length === 0" description="暂无订单" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { orderApi } from '@/api/modules'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const orders = ref([])
const activeTab = ref('all')

const tabs = [
  { label: '全部', value: 'all' },
  { label: '待支付', value: '0' },
  { label: '制作中', value: '2' },
  { label: '配送中', value: '3' },
  { label: '已完成', value: '4' }
]

const statusMap = {
  0: '待支付',
  1: '已支付',
  2: '制作中',
  3: '配送中',
  4: '已完成',
  5: '已取消'
}

const filteredOrders = computed(() => {
  if (activeTab.value === 'all') return orders.value
  return orders.value.filter(o => o.status === parseInt(activeTab.value))
})

onMounted(async () => {
  loading.value = true
  try {
    orders.value = await orderApi.list()
  } catch (e) {
    orders.value = []
  } finally {
    loading.value = false
  }
})

const payOrder = async (orderId) => {
  try {
    await orderApi.pay(orderId)
    ElMessage.success('支付成功')
    const order = orders.value.find(o => o.id === orderId)
    if (order) order.status = 1
  } catch (e) {
    ElMessage.error('支付失败')
  }
}

const cancelOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定取消该订单吗？', '提示')
    await orderApi.cancel(orderId)
    ElMessage.success('订单已取消')
    const order = orders.value.find(o => o.id === orderId)
    if (order) order.status = 5
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('取消失败')
  }
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.orders-page {
  padding-bottom: 80px;
}

.order-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 24px;
  background: $white;
  padding: 8px;
  border-radius: $radius-lg;
  box-shadow: $shadow-sm;
}

.tab-btn {
  flex: 1;
  padding: 12px;
  border: none;
  background: transparent;
  border-radius: $radius-md;
  font-weight: 500;
  cursor: pointer;
  transition: $transition;

  &:hover {
    background: $bg-warm;
  }

  &.active {
    background: $primary;
    color: $white;
  }
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: $white;
  border-radius: $radius-lg;
  overflow: hidden;
  box-shadow: $shadow-sm;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: $bg-warm;
  border-bottom: 1px solid $border;

  .order-no {
    font-size: 0.9rem;
    color: $text-light;
  }
}

.order-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;

  &.status-0 { background: #FEF0F0; color: #F56C6C; }
  &.status-1 { background: #F0F9EB; color: #67C23A; }
  &.status-2 { background: #F4F4F5; color: #909399; }
  &.status-3 { background: #FDF6EC; color: #E6A23C; }
  &.status-4 { background: #F0F9EB; color: #67C23A; }
  &.status-5 { background: #F4F4F5; color: #909399; }
}

.order-body {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  cursor: pointer;
  transition: $transition;

  &:hover {
    background: $bg-cream;
  }
}

.order-items {
  display: flex;
  align-items: center;
  gap: 16px;

  .item-placeholder {
    width: 60px;
    height: 60px;
    background: $bg-warm;
    border-radius: $radius-md;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 2rem;
  }

  .item-info {
    h4 {
      font-size: 1rem;
      margin-bottom: 4px;
    }
    p {
      font-size: 0.85rem;
      color: $text-light;
    }
  }
}

.order-info {
  text-align: right;

  .order-time {
    font-size: 0.85rem;
    color: $text-light;
    margin-bottom: 4px;
  }

  .order-price {
    font-size: 1.2rem;
    font-weight: 600;
    color: $primary;
  }
}

.order-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid $border;
}

@media (max-width: 640px) {
  .order-body {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .order-info {
    text-align: left;
    width: 100%;
    display: flex;
    justify-content: space-between;
  }
}
</style>
