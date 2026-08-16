<template>
  <div class="admin-page">
    <div class="page-header container">
      <h1 class="page-header__title">商家管理</h1>
    </div>

    <div class="container">
      <div class="admin-tabs">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="待处理订单" name="pending">
            <div class="order-list" v-loading="loading">
              <div class="order-card" v-for="order in pendingOrders" :key="order.id">
                <div class="order-card__header">
                  <span class="order-no">{{ order.orderNo }}</span>
                  <el-tag :type="statusTag(order.status)">{{ statusText(order.status) }}</el-tag>
                </div>
                <div class="order-card__items">
                  <div v-for="item in order.items" :key="item.cakeId" class="order-item">
                    <span>{{ item.cakeName }}</span>
                    <span>x{{ item.amount }} — ¥{{ (item.price * item.amount / 100).toFixed(0) }}</span>
                  </div>
                </div>
                <div class="order-card__footer">
                  <span class="total">合计：¥{{ (order.totalPrice / 100).toFixed(0) }}</span>
                  <div class="actions">
                    <el-button v-if="order.status === 1" type="primary" size="small" @click="handleAction(order.id, 'accept')">接单</el-button>
                    <el-button v-if="order.status === 2" type="warning" size="small" @click="handleAction(order.id, 'deliver')">发货</el-button>
                    <el-button v-if="order.status === 3" type="success" size="small" @click="handleAction(order.id, 'complete')">完成</el-button>
                  </div>
                </div>
              </div>
              <el-empty v-if="!loading && pendingOrders.length === 0" description="暂无待处理订单" />
            </div>
          </el-tab-pane>

          <el-tab-pane label="全部订单" name="all">
            <div class="order-list" v-loading="loadingAll">
              <div class="order-card" v-for="order in allOrders" :key="order.id">
                <div class="order-card__header">
                  <span class="order-no">{{ order.orderNo }}</span>
                  <el-tag :type="statusTag(order.status)" size="small">{{ statusText(order.status) }}</el-tag>
                </div>
                <div class="order-card__items">
                  <div v-for="item in order.items" :key="item.cakeId" class="order-item">
                    <span>{{ item.cakeName }} x{{ item.amount }}</span>
                  </div>
                </div>
              </div>
              <el-empty v-if="!loadingAll && allOrders.length === 0" description="暂无订单" />
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api/modules'
import { ElMessage, ElMessageBox } from 'element-plus'

const activeTab = ref('pending')
const loading = ref(false)
const loadingAll = ref(false)
const pendingOrders = ref([])
const allOrders = ref([])

const statusText = (s) => ['待支付','已支付','制作中','配送中','已完成','已取消'][s] || '未知'
const statusTag = (s) => ['info','success','warning','','success','danger'][s] || 'info'

const fetchOrders = async () => {
  loading.value = true
  loadingAll.value = true
  try {
    const all = await adminApi.list()
    allOrders.value = all || []
    pendingOrders.value = (all || []).filter(o => [1, 2, 3].includes(o.status))
  } catch (e) {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
    loadingAll.value = false
  }
}

const handleAction = async (orderId, action) => {
  const labels = { accept: '接单', deliver: '确认发货', complete: '确认完成' }
  try {
    await ElMessageBox.confirm(`确定${labels[action]}？`)
    await adminApi[action](orderId)
    ElMessage.success('操作成功')
    await fetchOrders()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

onMounted(fetchOrders)
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.admin-page { padding-bottom: 60px; }

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-top: 16px;
}

.order-card {
  background: $white;
  border-radius: $radius-lg;
  padding: 20px;
  box-shadow: $shadow-sm;

  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
  }

  &__items {
    border-top: 1px solid $border;
    padding-top: 12px;
    margin-bottom: 12px;
  }

  &__footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-top: 1px solid $border;
    padding-top: 12px;
  }
}

.order-no { font-weight: 600; }
.order-item {
  display: flex;
  justify-content: space-between;
  padding: 4px 0;
  font-size: 0.9rem;
  color: $text-light;
}

.total { font-weight: 600; color: $primary; }
</style>
