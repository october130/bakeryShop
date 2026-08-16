<template>
  <div class="profile-page">
    <div class="page-header container">
      <h1 class="page-header__title">个人中心</h1>
    </div>

    <div class="container">
      <div class="profile-content">
        <!-- 用户信息 -->
        <div class="user-card">
          <div class="avatar">
            <el-icon><UserFilled /></el-icon>
          </div>
          <div class="user-info">
            <h2>{{ userStore.userInfo?.username || '用户' }}</h2>
            <p>{{ userStore.userInfo?.phone || '未设置手机号' }}</p>
          </div>
        </div>

        <!-- 快捷入口 -->
        <div class="quick-links">
          <router-link to="/orders" class="link-card">
            <el-icon><List /></el-icon>
            <span>我的订单</span>
          </router-link>
          <router-link to="/cart" class="link-card">
            <el-icon><ShoppingCart /></el-icon>
            <span>购物车</span>
          </router-link>
          <a class="link-card" @click="logout">
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </a>
        </div>

        <!-- 订单统计 -->
        <div class="stats-card">
          <h3>订单统计</h3>
          <div class="stats-grid">
            <div class="stat-item">
              <span class="stat-num">{{ stats.total }}</span>
              <span class="stat-label">总订单</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">{{ stats.completed }}</span>
              <span class="stat-label">已完成</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">¥{{ stats.totalSpent }}</span>
              <span class="stat-label">消费金额</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { orderApi } from '@/api/modules'

const userStore = useUserStore()

const stats = reactive({
  total: 0,
  completed: 0,
  totalSpent: 0
})

onMounted(async () => {
  try {
    const orders = await orderApi.list()
    stats.total = orders.length
    stats.completed = orders.filter(o => o.status === 4).length
    stats.totalSpent = orders
      .filter(o => o.status === 4)
      .reduce((sum, o) => sum + o.totalPrice, 0)
  } catch (e) {
    // 使用默认值
  }
})

const logout = () => {
  userStore.logout()
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.profile-page {
  padding-bottom: 80px;
}

.profile-content {
  max-width: 600px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 24px;
  background: linear-gradient(135deg, $primary, $primary-light);
  border-radius: $radius-xl;
  padding: 32px;
  color: $white;

  .avatar {
    width: 80px;
    height: 80px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 2.5rem;
  }

  .user-info {
    h2 {
      font-size: 1.5rem;
      margin-bottom: 4px;
    }

    p {
      opacity: 0.9;
    }
  }
}

.quick-links {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.link-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 24px;
  background: $white;
  border-radius: $radius-lg;
  box-shadow: $shadow-sm;
  cursor: pointer;
  transition: $transition;
  text-decoration: none;
  color: $text-dark;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-md;
    color: $primary;
  }

  .el-icon {
    font-size: 1.5rem;
  }

  span {
    font-size: 0.9rem;
  }
}

.stats-card {
  background: $white;
  border-radius: $radius-lg;
  padding: 24px;
  box-shadow: $shadow-sm;

  h3 {
    font-size: 1.1rem;
    margin-bottom: 20px;
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background: $bg-warm;
  border-radius: $radius-md;

  .stat-num {
    display: block;
    font-size: 1.5rem;
    font-weight: 600;
    color: $primary;
    margin-bottom: 4px;
  }

  .stat-label {
    font-size: 0.85rem;
    color: $text-light;
  }
}
</style>
