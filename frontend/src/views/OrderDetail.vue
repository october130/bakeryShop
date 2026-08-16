<template>
  <div class="order-detail-page">
    <div class="container">
      <el-page-header @back="$router.back()" title="返回" />

      <div class="detail-content" v-loading="loading">
        <!-- 订单状态 -->
        <div class="status-card">
          <div class="status-icon">
            <el-icon v-if="order.status === 0"><Clock /></el-icon>
            <el-icon v-else-if="order.status === 1"><Check /></el-icon>
            <el-icon v-else-if="order.status === 2"><Loading /></el-icon>
            <el-icon v-else-if="order.status === 3"><Van /></el-icon>
            <el-icon v-else-if="order.status === 4"><CircleCheck /></el-icon>
            <el-icon v-else><Close /></el-icon>
          </div>
          <h2 class="status-text">{{ statusMap[order.status] }}</h2>
          <p class="status-desc">{{ statusDesc[order.status] }}</p>
        </div>

        <!-- 配送信息 -->
        <div class="info-card">
          <h3>配送信息</h3>
          <div class="info-row">
            <span class="label">收货地址</span>
            <span class="value">{{ order.address }}</span>
          </div>
          <div class="info-row">
            <span class="label">联系电话</span>
            <span class="value">{{ order.phone }}</span>
          </div>
          <div class="info-row">
            <span class="label">配送时间</span>
            <span class="value">{{ order.deliverTime || '尽快配送' }}</span>
          </div>
        </div>

        <!-- 订单商品 -->
        <div class="info-card">
          <h3>订单商品</h3>
          <div class="cake-item" v-for="item in orderItems" :key="item.cakeId">
            <div class="item-image">🎂</div>
            <div class="item-info">
              <h4>{{ item.cakeName }}</h4>
              <p v-if="item.customInfo">{{ item.customInfo }}</p>
            </div>
            <div class="item-price">
              <span>×{{ item.amount }}</span>
              <span class="price">¥{{ (item.price / 100).toFixed(0) }}</span>
            </div>
          </div>
        </div>

        <!-- 订单金额 -->
        <div class="price-card">
          <div class="price-row">
            <span>商品总额</span>
            <span>¥{{ order.totalPrice }}</span>
          </div>
          <div class="price-row">
            <span>配送费</span>
            <span>¥0</span>
          </div>
          <div class="price-row total">
            <span>实付金额</span>
            <span class="total-price">¥{{ order.totalPrice }}</span>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="action-bar">
          <template v-if="order.status === 0">
            <el-button @click="cancelOrder">取消订单</el-button>
            <el-button type="primary" @click="payOrder">立即支付</el-button>
          </template>
          <template v-else-if="order.status === 4">
            <el-button type="primary" @click="$router.push(`/review/${order.id}`)">去评价</el-button>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { orderApi } from '@/api/modules'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const order = ref({})
const orderItems = ref([])

const statusMap = {
  0: '待支付',
  1: '已支付',
  2: '制作中',
  3: '配送中',
  4: '已完成',
  5: '已取消'
}

const statusDesc = {
  0: '请尽快完成支付，订单将在支付后开始制作',
  1: '支付成功，等待商家接单',
  2: '商家正在为您精心制作',
  3: '蛋糕正在配送中，请保持电话畅通',
  4: '订单已完成，祝您用餐愉快',
  5: '订单已取消'
}

onMounted(async () => {
  loading.value = true
  try {
    const data = await orderApi.detail(route.params.id)
    order.value = data
    orderItems.value = data.items || []
  } catch (e) {
    // 模拟数据
    order.value = {
      id: route.params.id,
      orderNo: 'ORDER_20240120001',
      status: 0,
      totalPrice: 168,
      address: '北京市朝阳区xxx街道xxx号',
      phone: '138****8888',
      deliverTime: '2024-01-20 14:00'
    }
    orderItems.value = [
      { cakeId: 1, cakeName: '经典草莓奶油', price: 168, amount: 1, customInfo: '8寸，生日快乐' }
    ]
  } finally {
    loading.value = false
  }
})

const payOrder = async () => {
  try {
    await orderApi.pay(order.value.id)
    ElMessage.success('支付成功')
    order.value.status = 1
  } catch (e) {
    ElMessage.error('支付失败')
  }
}

const cancelOrder = async () => {
  try {
    await ElMessageBox.confirm('确定取消该订单吗？')
    await orderApi.cancel(order.value.id)
    ElMessage.success('订单已取消')
    order.value.status = 5
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('取消失败')
  }
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.order-detail-page {
  padding: 32px 0 80px;
}

.el-page-header {
  margin-bottom: 32px;
}

.detail-content {
  max-width: 700px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.status-card {
  text-align: center;
  padding: 40px;
  background: linear-gradient(135deg, $primary, $primary-light);
  border-radius: $radius-xl;
  color: $white;

  .status-icon {
    font-size: 3rem;
    margin-bottom: 16px;
  }

  .status-text {
    font-size: 1.5rem;
    margin-bottom: 8px;
  }

  .status-desc {
    opacity: 0.9;
  }
}

.info-card {
  background: $white;
  border-radius: $radius-lg;
  padding: 24px;
  box-shadow: $shadow-sm;

  h3 {
    font-size: 1.1rem;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 1px solid $border;
  }
}

.info-row {
  display: flex;
  padding: 12px 0;

  .label {
    width: 100px;
    color: $text-light;
  }

  .value {
    flex: 1;
  }
}

.cake-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid $border;

  &:last-child {
    border-bottom: none;
  }

  .item-image {
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
    flex: 1;

    h4 {
      font-size: 1rem;
      margin-bottom: 4px;
    }

    p {
      font-size: 0.85rem;
      color: $text-light;
    }
  }

  .item-price {
    text-align: right;

    span:first-child {
      display: block;
      font-size: 0.85rem;
      color: $text-light;
      margin-bottom: 4px;
    }

    .price {
      font-size: 1.1rem;
      font-weight: 600;
      color: $primary;
    }
  }
}

.price-card {
  background: $white;
  border-radius: $radius-lg;
  padding: 24px;
  box-shadow: $shadow-sm;
}

.price-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  color: $text-light;

  &.total {
    padding-top: 16px;
    margin-top: 8px;
    border-top: 1px solid $border;
    color: $text-dark;
    font-size: 1.1rem;

    .total-price {
      font-size: 1.5rem;
      font-weight: 600;
      color: $primary;
    }
  }
}

.action-bar {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 20px;
}
</style>
