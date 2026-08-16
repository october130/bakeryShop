<template>
  <div class="cake-list-page">
    <div class="page-header">
      <div class="container">
        <h1 class="page-header__title">全部蛋糕</h1>
        <p class="page-header__subtitle">精选优质蛋糕，每一款都是匠心之作</p>
      </div>
    </div>

    <div class="container">
      <div class="cake-grid" v-loading="loading">
        <div
          class="product-card"
          v-for="cake in cakes"
          :key="cake.id"
          @click="$router.push(`/cake/${cake.id}`)"
        >
          <div class="product-card__image">
            <img :src="cake.image || defaultImage" :alt="cake.name" @error="handleImageError" />
            <span class="product-card__badge" v-if="cake.badge">{{ cake.badge }}</span>
            <div class="product-card__overlay">
              <button class="btn btn--primary btn--sm" @click.stop="addToCart(cake)">
                <el-icon><ShoppingCart /></el-icon> 加入购物车
              </button>
            </div>
          </div>
          <div class="product-card__info">
            <h3 class="product-card__name">{{ cake.name }}</h3>
            <p class="product-card__desc">{{ cake.description }}</p>
            <div class="product-card__footer">
              <span class="product-card__price">
                <em>¥</em>{{ (cake.price / 100).toFixed(0) }}
              </span>
              <span class="product-card__sold">{{ cake.sold || 0 }}人已购</span>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && cakes.length === 0" description="暂无蛋糕" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useCartStore } from '@/stores/cart'
import { cakeApi } from '@/api/modules'

const cartStore = useCartStore()
const loading = ref(false)
const cakes = ref([])

const defaultImage = 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400&h=400&fit=crop'

const mockCakes = [
  { id: 1, name: '经典草莓奶油蛋糕', description: '新鲜草莓搭配轻盈奶油，入口即化', price: 168, sold: 2680, image: 'https://images.unsplash.com/photo-1565958011703-44f9829ba187?w=400&h=400&fit=crop' },
  { id: 2, name: '黑森林巧克力蛋糕', description: '浓郁巧克力与樱桃的经典组合', price: 198, sold: 1856, image: 'https://images.unsplash.com/photo-1606890737304-57a1ca8a5b62?w=400&h=400&fit=crop' },
  { id: 3, name: '芒果慕斯蛋糕', description: '热带水果的清新享受，丝滑细腻', price: 178, sold: 1423, image: 'https://images.unsplash.com/photo-1587668178277-295251f900ce?w=400&h=400&fit=crop' },
  { id: 4, name: '提拉米苏', description: '意式经典，浓郁咖啡与马斯卡彭芝士', price: 208, sold: 998, image: 'https://images.unsplash.com/photo-1571877227200-a0d98ea607e9?w=400&h=400&fit=crop' },
  { id: 5, name: '抹茶红豆蛋糕', description: '日式风情，细腻绵密，甜而不腻', price: 158, sold: 1120, image: 'https://images.unsplash.com/photo-1607478900766-efe13248b125?w=400&h=400&fit=crop' },
  { id: 6, name: '纽约芝士蛋糕', description: '醇厚浓郁，经典美式风味', price: 188, sold: 876, image: 'https://images.unsplash.com/photo-1524351199678-941a58a3df50?w=400&h=400&fit=crop' },
  { id: 7, name: '水果拼盘蛋糕', description: '多种时令水果，色彩缤纷', price: 238, sold: 654, image: 'https://images.unsplash.com/photo-1488477181946-6428a0291777?w=400&h=400&fit=crop' },
  { id: 8, name: '巧克力熔岩蛋糕', description: '外酥内软，巧克力控的最爱', price: 148, sold: 1567, image: 'https://images.unsplash.com/photo-1624353365286-3f8d62daad51?w=400&h=400&fit=crop' }
]

onMounted(async () => {
  loading.value = true
  try {
    const data = await cakeApi.list(1)
    if (data && data.length > 0) {
      cakes.value = data
    } else {
      cakes.value = mockCakes
    }
  } catch (e) {
    cakes.value = mockCakes
  } finally {
    loading.value = false
  }
})

const handleImageError = (e) => {
  e.target.src = defaultImage
}

const addToCart = (cake) => {
  cartStore.addToCart({
    cakeId: cake.id,
    amount: 1
  })
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.cake-list-page {
  padding-bottom: 80px;
}

.cake-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  padding: 32px 0;
}

.product-card {
  background: $white;
  border-radius: $radius-md;
  overflow: hidden;
  cursor: pointer;
  transition: $transition;
  border: 1px solid $border;

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

@media (max-width: 1024px) {
  .cake-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .cake-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
