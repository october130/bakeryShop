<template>
  <div class="home">
    <!-- Hero Banner -->
    <section class="hero">
      <div class="hero__slider">
        <div class="hero__slide active">
          <div class="hero__bg" style="background-image: url('https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=1400&h=500&fit=crop')"></div>
          <div class="container hero__content">
            <div class="hero__text">
              <span class="hero__tag">精选推荐</span>
              <h2 class="hero__title">每一口都是<br>幸福的味道</h2>
              <p class="hero__desc">甄选优质原料，匠心手工制作，为您的每一个重要时刻增添甜蜜</p>
              <div class="hero__actions">
                <router-link to="/cakes" class="btn btn--primary btn--lg">立即选购</router-link>
                <router-link to="/bakery" class="btn btn--outline btn--lg">查看门店</router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
      <!-- 搜索框 -->
      <div class="hero__search">
        <div class="container">
          <div class="search-box">
            <el-input
              v-model="searchText"
              placeholder="搜索蛋糕、口味、场景..."
              size="large"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <button class="btn btn--primary" @click="handleSearch">搜索</button>
          </div>
        </div>
      </div>
    </section>

    <!-- 分类导航 -->
    <section class="categories">
      <div class="container">
        <div class="category-grid">
          <div class="category-item" v-for="cat in categories" :key="cat.name" @click="$router.push({ path: '/cakes', query: { flavor: cat.name } })">
            <div class="category-icon" :style="{ background: cat.color }">
              <el-icon :size="28" color="#fff"><component :is="cat.icon" /></el-icon>
            </div>
            <span class="category-name">{{ cat.name }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 热销蛋糕 -->
    <section class="products">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">人气热销</h2>
          <p class="section-desc">最受欢迎的蛋糕，每一款都值得品尝</p>
        </div>
        <div class="product-grid">
          <div
            class="product-card"
            v-for="cake in popularCakes"
            :key="cake.id"
            @click="$router.push(`/cake/${cake.id}`)"
          >
            <div class="product-card__image">
              <img :src="cake.image" :alt="cake.name" />
              <span class="product-card__badge" v-if="cake.badge">{{ cake.badge }}</span>
              <div class="product-card__overlay">
                <button class="btn btn--primary btn--sm" @click.stop="addToCart(cake)">
                  <el-icon><ShoppingCart /></el-icon> 加入购物车
                </button>
              </div>
            </div>
            <div class="product-card__info">
              <h3 class="product-card__name">{{ cake.name }}</h3>
              <p class="product-card__desc">{{ cake.desc }}</p>
              <div class="product-card__footer">
                <span class="product-card__price">
                  <em>¥</em>{{ (cake.price / 100).toFixed(0) }}
                </span>
                <span class="product-card__sold">{{ cake.sold }}人已购</span>
              </div>
            </div>
          </div>
        </div>
        <div class="section-more">
          <router-link to="/cakes" class="btn btn--outline">查看全部蛋糕</router-link>
        </div>
      </div>
    </section>

    <!-- 服务保障 -->
    <section class="services">
      <div class="container">
        <div class="service-grid">
          <div class="service-item" v-for="service in services" :key="service.title">
            <div class="service-icon">
              <el-icon :size="32" :color="service.color"><component :is="service.icon" /></el-icon>
            </div>
            <div class="service-text">
              <h4>{{ service.title }}</h4>
              <p>{{ service.desc }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 场景推荐 -->
    <section class="occasions">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">场景推荐</h2>
          <p class="section-desc">为每个特别时刻，挑选合适的蛋糕</p>
        </div>
        <div class="occasion-grid">
          <div
            class="occasion-card"
            v-for="occasion in occasions"
            :key="occasion.name"
            @click="$router.push({ path: '/cakes', query: { occasion: occasion.name } })"
          >
            <div class="occasion-card__image">
              <img :src="occasion.image" :alt="occasion.name" />
              <div class="occasion-card__overlay">
                <h3>{{ occasion.name }}</h3>
                <p>{{ occasion.desc }}</p>
                <span class="occasion-card__link">立即选购 <el-icon><ArrowRight /></el-icon></span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { cakeApi } from '@/api/modules'

const router = useRouter()
const cartStore = useCartStore()
const searchText = ref('')

const categories = [
  { name: '生日蛋糕', icon: 'Present', color: '#FF6B6B' },
  { name: '水果蛋糕', icon: 'Apple', color: '#51CF66' },
  { name: '巧克力', icon: 'Coffee', color: '#8B4513' },
  { name: '慕斯', icon: 'IceCream', color: '#FF8787' },
  { name: '芝士', icon: 'Food', color: '#FFC078' },
  { name: '提拉米苏', icon: 'CoffeeCup', color: '#A0522D' },
  { name: '定制', icon: 'EditPen', color: '#B197FC' },
  { name: '新品', icon: 'Star', color: '#FFD43B' }
]

const services = [
  { icon: 'Medal', title: '品质保证', desc: '甄选优质原料', color: '#C41A30' },
  { icon: 'Van', title: '新鲜配送', desc: '2小时急速达', color: '#1890FF' },
  { icon: 'Service', title: '贴心售后', desc: '24小时客服', color: '#52C41A' },
  { icon: 'Wallet', title: '安心支付', desc: '多种支付方式', color: '#FAAD14' }
]

const occasions = [
  {
    name: '生日派对',
    desc: '让生日更加甜蜜难忘',
    image: 'https://images.unsplash.com/photo-1558636508-e0db3814bd1d?w=400&h=300&fit=crop'
  },
  {
    name: '浪漫情人节',
    desc: '用甜蜜表达爱意',
    image: 'https://images.unsplash.com/photo-1543006425-5f703a69ec5c?w=400&h=300&fit=crop'
  },
  {
    name: '婚礼庆典',
    desc: '见证幸福的时刻',
    image: 'https://images.unsplash.com/photo-1535254973040-607b474cb50d?w=400&h=300&fit=crop'
  },
  {
    name: '商务活动',
    desc: '高端大气的选择',
    image: 'https://images.unsplash.com/photo-1464349095431-e9a2128516f3?w=400&h=300&fit=crop'
  }
]

const popularCakes = ref([])

const defaultCakes = [
  {
    id: 1,
    name: '经典草莓奶油蛋糕',
    desc: '新鲜草莓搭配轻盈奶油，入口即化',
    price: 168,
    sold: 2680,
    badge: '热销',
    image: 'https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=400&h=400&fit=crop'
  },
  {
    id: 2,
    name: '黑森林巧克力蛋糕',
    desc: '浓郁巧克力与樱桃的经典组合',
    price: 198,
    sold: 1856,
    badge: '人气',
    image: 'https://images.unsplash.com/photo-1606890737304-57a1ca8a5b62?w=400&h=400&fit=crop'
  },
  {
    id: 3,
    name: '芒果慕斯蛋糕',
    desc: '热带水果的清新享受，丝滑细腻',
    price: 178,
    sold: 1423,
    badge: '新品',
    image: 'https://images.unsplash.com/photo-1587668178277-295251f900ce?w=400&h=400&fit=crop'
  },
  {
    id: 4,
    name: '提拉米苏',
    desc: '意式经典，浓郁咖啡与马斯卡彭芝士',
    price: 208,
    sold: 998,
    image: 'https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=400&h=400&fit=crop'
  },
  {
    id: 5,
    name: '抹茶红豆蛋糕',
    desc: '日式风情，细腻绵密，甜而不腻',
    price: 158,
    sold: 1120,
    image: 'https://images.unsplash.com/photo-1607478900766-efe13248b125?w=400&h=400&fit=crop'
  },
  {
    id: 6,
    name: '纽约芝士蛋糕',
    desc: '醇厚浓郁，经典美式风味',
    price: 188,
    sold: 876,
    image: 'https://images.unsplash.com/photo-1524351199678-941a58a3df50?w=400&h=400&fit=crop'
  },
  {
    id: 7,
    name: '水果拼盘蛋糕',
    desc: '多种时令水果，色彩缤纷',
    price: 238,
    sold: 654,
    image: 'https://images.unsplash.com/photo-1488477181946-6428a0291777?w=400&h=400&fit=crop'
  },
  {
    id: 8,
    name: '巧克力熔岩蛋糕',
    desc: '外酥内软，巧克力控的最爱',
    price: 148,
    sold: 1567,
    badge: '必吃',
    image: 'https://images.unsplash.com/photo-1624353365286-3f8d62daad51?w=400&h=400&fit=crop'
  }
]

onMounted(async () => {
  try {
    const data = await cakeApi.list(1)
    if (data && data.length > 0) {
      popularCakes.value = data.map((cake, i) => ({
        ...cake,
        image: defaultCakes[i % defaultCakes.length]?.image || 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400&h=400&fit=crop',
        desc: cake.description,
        sold: cake.sold || Math.floor(Math.random() * 2000)
      }))
    } else {
      popularCakes.value = defaultCakes
    }
  } catch (e) {
    popularCakes.value = defaultCakes
  }
})

const handleSearch = () => {
  if (searchText.value.trim()) {
    router.push({ path: '/cakes', query: { search: searchText.value } })
  }
}

const addToCart = (cake) => {
  cartStore.addToCart({
    cakeId: cake.id,
    cakeName: cake.name,
    price: cake.price,
    amount: 1,
    bakeryId: 1
  })
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

// Hero
.hero {
  position: relative;

  &__slide {
    position: relative;
    height: 500px;
    overflow: hidden;
  }

  &__bg {
    position: absolute;
    inset: 0;
    background-size: cover;
    background-position: center;

    &::after {
      content: '';
      position: absolute;
      inset: 0;
      background: linear-gradient(90deg, rgba(0,0,0,0.5) 0%, rgba(0,0,0,0.1) 100%);
    }
  }

  &__content {
    position: relative;
    z-index: 1;
    height: 100%;
    display: flex;
    align-items: center;
  }

  &__text {
    max-width: 500px;
    color: $white;
  }

  &__tag {
    display: inline-block;
    padding: 4px 12px;
    background: $primary;
    font-size: 12px;
    border-radius: 2px;
    margin-bottom: 16px;
  }

  &__title {
    font-size: 42px;
    font-weight: 700;
    line-height: 1.3;
    margin-bottom: 16px;
  }

  &__desc {
    font-size: 16px;
    opacity: 0.9;
    margin-bottom: 32px;
    line-height: 1.8;
  }

  &__actions {
    display: flex;
    gap: 16px;

    .btn--outline {
      border-color: $white;
      color: $white;

      &:hover {
        background: $white;
        color: $text-dark;
      }
    }
  }

  &__search {
    position: absolute;
    bottom: -28px;
    left: 0;
    right: 0;
    z-index: 2;
  }
}

.search-box {
  display: flex;
  background: $white;
  border-radius: $radius-md;
  box-shadow: $shadow-card;
  overflow: hidden;

  .el-input {
    flex: 1;
  }

  .el-input__wrapper {
    box-shadow: none !important;
    padding: 0 16px;
  }

  .btn {
    border-radius: 0;
    padding: 0 32px;
  }
}

// 分类
.categories {
  padding: 60px 0 40px;
}

.category-grid {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: $transition;

  &:hover {
    transform: translateY(-4px);

    .category-icon {
      box-shadow: $shadow-hover;
    }
  }
}

.category-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: $transition;
}

.category-name {
  font-size: 13px;
  color: $text-body;
  font-weight: 500;
}

// 商品
.products {
  padding: 60px 0;
  background: $bg-light;
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.section-title {
  font-size: 28px;
  color: $text-dark;
  margin-bottom: 8px;
  position: relative;
  display: inline-block;

  &::after {
    content: '';
    display: block;
    width: 40px;
    height: 3px;
    background: $primary;
    margin: 12px auto 0;
  }
}

.section-desc {
  color: $text-light;
  font-size: 14px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.product-card {
  background: $white;
  border-radius: $radius-md;
  overflow: hidden;
  cursor: pointer;
  transition: $transition;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-hover;

    .product-card__overlay {
      opacity: 1;
    }

    .product-card__image img {
      transform: scale(1.05);
    }
  }

  &__image {
    position: relative;
    aspect-ratio: 1;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s ease;
    }
  }

  &__badge {
    position: absolute;
    top: 12px;
    left: 12px;
    padding: 4px 10px;
    background: $primary;
    color: $white;
    font-size: 12px;
    font-weight: 500;
    border-radius: 2px;
    z-index: 1;
  }

  &__overlay {
    position: absolute;
    inset: 0;
    background: rgba(0, 0, 0, 0.3);
    display: flex;
    align-items: center;
    justify-content: center;
    opacity: 0;
    transition: $transition;
  }

  &__info {
    padding: 16px;
  }

  &__name {
    font-size: 15px;
    font-weight: 600;
    margin-bottom: 6px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  &__desc {
    font-size: 13px;
    color: $text-light;
    margin-bottom: 12px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  &__footer {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
  }

  &__price {
    color: $primary;
    font-size: 18px;
    font-weight: 700;

    em {
      font-size: 13px;
      font-weight: 500;
    }
  }

  &__sold {
    font-size: 12px;
    color: $text-muted;
  }
}

.section-more {
  text-align: center;
  margin-top: 40px;
}

// 服务
.services {
  padding: 48px 0;
  border-bottom: 1px solid $border;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.service-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  border-radius: $radius-md;
  transition: $transition;

  &:hover {
    background: $bg-light;
  }
}

.service-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: $bg-light;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.service-text {
  h4 {
    font-size: 15px;
    margin-bottom: 4px;
  }

  p {
    font-size: 13px;
    color: $text-light;
  }
}

// 场景
.occasions {
  padding: 60px 0;
}

.occasion-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.occasion-card {
  cursor: pointer;

  &__image {
    position: relative;
    border-radius: $radius-md;
    overflow: hidden;
    aspect-ratio: 4/3;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s ease;
    }
  }

  &__overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(transparent 40%, rgba(0,0,0,0.7));
    display: flex;
    flex-direction: column;
    justify-content: flex-end;
    padding: 20px;
    color: $white;

    h3 {
      font-size: 18px;
      margin-bottom: 4px;
    }

    p {
      font-size: 13px;
      opacity: 0.9;
      margin-bottom: 12px;
    }
  }

  &__link {
    font-size: 13px;
    display: flex;
    align-items: center;
    gap: 4px;
    opacity: 0;
    transform: translateY(10px);
    transition: $transition;
  }

  &:hover {
    .occasion-card__image img {
      transform: scale(1.08);
    }

    .occasion-card__link {
      opacity: 1;
      transform: translateY(0);
    }
  }
}

@media (max-width: 1024px) {
  .product-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .occasion-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero__slide {
    height: 400px;
  }

  .hero__title {
    font-size: 32px;
  }

  .product-grid,
  .service-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .category-grid {
    flex-wrap: wrap;
    justify-content: center;
  }
}
</style>
