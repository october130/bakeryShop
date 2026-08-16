<template>
  <div class="layout">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header__top">
        <div class="container header__top-inner">
          <span>欢迎来到蛋糕烘焙坊</span>
          <div class="header__top-links">
            <a href="#">关于我们</a>
            <span class="divider">|</span>
            <a href="#">配送说明</a>
            <span class="divider">|</span>
            <a href="#">客服电话: 400-888-8888</a>
          </div>
        </div>
      </div>
      <div class="header__main">
        <div class="container header__main-inner">
          <router-link to="/" class="logo">
            <div class="logo__icon">
              <el-icon :size="28"><Food /></el-icon>
            </div>
            <div class="logo__text">
              <h1>蛋糕烘焙坊</h1>
              <p>CAKE BAKERY</p>
            </div>
          </router-link>

          <nav class="main-nav">
            <router-link to="/" class="nav-link" active-class="active" exact>首页</router-link>
            <router-link to="/cakes" class="nav-link" active-class="active">全部蛋糕</router-link>
            <router-link to="/flash-sale" class="nav-link" active-class="active">限时抢购</router-link>
            <router-link to="/bakery" class="nav-link" active-class="active">门店</router-link>
            <router-link to="/orders" class="nav-link" active-class="active">我的订单</router-link>
          </nav>

          <div class="header__actions">
            <router-link to="/cart" class="action-icon">
              <el-icon :size="22"><ShoppingCart /></el-icon>
              <span class="cart-badge" v-if="cartStore.totalCount > 0">{{ cartStore.totalCount }}</span>
              <span>购物车</span>
            </router-link>

            <template v-if="userStore.isLoggedIn">
              <el-dropdown trigger="click">
                <button class="user-btn">
                  <el-icon :size="20"><User /></el-icon>
                  <span>{{ userStore.userInfo?.username || '我的' }}</span>
                  <el-icon :size="12"><ArrowDown /></el-icon>
                </button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="$router.push('/profile')">
                      <el-icon><UserFilled /></el-icon>个人中心
                    </el-dropdown-item>
                    <el-dropdown-item @click="$router.push('/orders')">
                      <el-icon><List /></el-icon>我的订单
                    </el-dropdown-item>
                    <el-dropdown-item @click="$router.push('/admin')">
                      <el-icon><Setting /></el-icon>商家管理
                    </el-dropdown-item>
                    <el-dropdown-item divided @click="userStore.logout()">
                      <el-icon><SwitchButton /></el-icon>退出登录
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>

            <template v-else>
              <router-link to="/login" class="btn btn--outline btn--sm">登录</router-link>
              <router-link to="/register" class="btn btn--primary btn--sm">注册</router-link>
            </template>
          </div>
        </div>
      </div>
    </header>

    <main class="main">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="container">
        <div class="footer__top">
          <div class="footer__brand">
            <h3>蛋糕烘焙坊</h3>
            <p>用心烘焙，传递甜蜜</p>
            <div class="footer__contact">
              <p><el-icon><Phone /></el-icon> 400-888-8888</p>
              <p><el-icon><Message /></el-icon> service@cakebakery.com</p>
            </div>
          </div>
          <div class="footer__links">
            <div class="link-group">
              <h4>关于我们</h4>
              <a href="#">品牌故事</a>
              <a href="#">门店地址</a>
              <a href="#">加入我们</a>
            </div>
            <div class="link-group">
              <h4>帮助中心</h4>
              <a href="#">配送说明</a>
              <a href="#">退换政策</a>
              <a href="#">常见问题</a>
            </div>
            <div class="link-group">
              <h4>服务保障</h4>
              <a href="#">品质承诺</a>
              <a href="#">隐私政策</a>
              <a href="#">用户协议</a>
            </div>
          </div>
        </div>
        <div class="footer__bottom">
          <p>© 2024 蛋糕烘焙坊 CAKE BAKERY 版权所有</p>
        </div>
      </div>
    </footer>

    <AiAssistant />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { useCartStore } from '@/stores/cart'
import AiAssistant from '@/components/AiAssistant.vue'

const userStore = useUserStore()
const cartStore = useCartStore()

onMounted(() => {
  if (userStore.isLoggedIn) {
    cartStore.fetchCart()
  }
})
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

// 顶部信息栏
.header__top {
  background: $bg-light;
  border-bottom: 1px solid $border;
  font-size: 12px;
  color: $text-light;

  &-inner {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 36px;
  }

  &-links {
    display: flex;
    align-items: center;
    gap: 8px;

    a {
      color: $text-light;
      font-size: 12px;

      &:hover {
        color: $primary;
      }
    }

    .divider {
      color: $border;
    }
  }
}

// 主导航
.header__main {
  background: $white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  position: sticky;
  top: 0;
  z-index: 100;

  &-inner {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: $header-height;
  }
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;

  &__icon {
    width: 48px;
    height: 48px;
    background: $primary;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: $white;
  }

  &__text {
    h1 {
      font-size: 22px;
      color: $text-dark;
      line-height: 1.2;
    }

    p {
      font-size: 11px;
      color: $text-light;
      letter-spacing: 2px;
    }
  }
}

.main-nav {
  display: flex;
  gap: 4px;
}

.nav-link {
  padding: 8px 20px;
  font-size: 15px;
  font-weight: 500;
  color: $text-body;
  border-radius: $radius-sm;
  transition: $transition;

  &:hover {
    color: $primary;
    background: rgba($primary, 0.04);
  }

  &.active {
    color: $primary;
    background: rgba($primary, 0.08);
  }
}

.header__actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.action-icon {
  display: flex;
  align-items: center;
  gap: 6px;
  color: $text-body;
  font-size: 14px;
  position: relative;
  cursor: pointer;

  &:hover {
    color: $primary;
  }

  .cart-badge {
    position: absolute;
    top: -8px;
    right: -12px;
    min-width: 18px;
    height: 18px;
    padding: 0 5px;
    background: $primary;
    color: $white;
    font-size: 11px;
    font-weight: 600;
    border-radius: 9px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.user-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: none;
  border: none;
  cursor: pointer;
  color: $text-body;
  font-size: 14px;

  &:hover {
    color: $primary;
  }
}

// 页脚
.footer {
  background: #2C2C2C;
  color: rgba($white, 0.7);
  margin-top: auto;

  &__top {
    display: flex;
    justify-content: space-between;
    padding: 48px 0 40px;
    gap: 60px;
  }

  &__brand {
    h3 {
      color: $white;
      font-size: 20px;
      margin-bottom: 8px;
    }

    > p {
      font-size: 14px;
      margin-bottom: 20px;
    }
  }

  &__contact {
    p {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 13px;
      margin-bottom: 6px;
    }
  }

  &__links {
    display: flex;
    gap: 60px;
  }

  .link-group {
    h4 {
      color: $white;
      font-size: 15px;
      margin-bottom: 16px;
    }

    a {
      display: block;
      color: rgba($white, 0.6);
      font-size: 13px;
      margin-bottom: 10px;
      transition: $transition;

      &:hover {
        color: $white;
      }
    }
  }

  &__bottom {
    border-top: 1px solid rgba($white, 0.1);
    padding: 20px 0;
    text-align: center;
    font-size: 12px;
    color: rgba($white, 0.4);
  }
}
</style>
