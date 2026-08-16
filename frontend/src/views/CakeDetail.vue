<template>
  <div class="cake-detail-page">
    <div class="container">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item :to="{ path: '/cakes' }">蛋糕</el-breadcrumb-item>
        <el-breadcrumb-item>{{ cake.name }}</el-breadcrumb-item>
      </el-breadcrumb>

      <div class="detail-content" v-loading="loading">
        <div class="detail-gallery">
          <div class="main-image">
            <img :src="cake.image || defaultImage" :alt="cake.name" @error="handleImageError" class="cake-image" />
          </div>
        </div>

        <div class="detail-info">
          <h1 class="detail-title">{{ cake.name }}</h1>
          <p class="detail-desc">{{ cake.description }}</p>

          <div class="detail-price">
            <span class="price-label">价格</span>
            <span class="price-value">¥{{ (cake.price / 100).toFixed(0) }}</span>
          </div>

          <div class="detail-meta">
            <div class="meta-item">
              <span class="meta-label">口味</span>
              <span class="meta-value">{{ cake.flavor }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">已售</span>
              <span class="meta-value">{{ cake.sold || 0 }} 份</span>
            </div>
          </div>

          <!-- 定制选项 -->
          <div class="customization">
            <h3>定制选项</h3>

            <div class="custom-field">
              <label>蛋糕尺寸</label>
              <el-radio-group v-model="selectedSize">
                <el-radio-button
                  v-for="size in sizes"
                  :key="size.value"
                  :value="size.value"
                >
                  {{ size.label }}
                </el-radio-button>
              </el-radio-group>
            </div>

            <div class="custom-field">
              <label>祝福语</label>
              <el-input
                v-model="blessing"
                placeholder="请输入祝福语（选填）"
                maxlength="50"
                show-word-limit
              />
            </div>

            <div class="custom-field">
              <label>数量</label>
              <el-input-number v-model="quantity" :min="1" :max="10" />
            </div>
          </div>

          <div class="detail-actions">
            <button class="btn btn--primary btn--lg" @click="addToCart">
              <el-icon><ShoppingCart /></el-icon>
              加入购物车
            </button>
            <button class="btn btn--accent btn--lg" @click="buyNow">
              立即购买
            </button>
          </div>
        </div>
      </div>

      <!-- 评价区域 -->
      <div class="review-section">
        <h2 class="section-title">用户评价</h2>
        <div class="review-list">
          <div class="review-item" v-for="review in reviews" :key="review.id">
            <div class="review-header">
              <el-rate v-model="review.score" disabled />
              <span class="review-time">{{ review.createTime }}</span>
            </div>
            <p class="review-content">{{ review.content }}</p>
            <div class="review-reply" v-if="review.replyContent">
              <strong>商家回复：</strong>{{ review.replyContent }}
            </div>
          </div>
        </div>
        <el-empty v-if="reviews.length === 0" description="暂无评价" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { ElMessage } from 'element-plus'
import { cakeApi, reviewApi } from '@/api/modules'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const loading = ref(false)
const cake = ref({})
const reviews = ref([])

const defaultImage = 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400&h=400&fit=crop'
const handleImageError = (e) => { e.target.src = defaultImage }

const selectedSize = ref('6')
const blessing = ref('')
const quantity = ref(1)

const sizes = [
  { label: '6寸', value: '6' },
  { label: '8寸', value: '8' },
  { label: '10寸', value: '10' },
  { label: '12寸', value: '12' }
]

onMounted(async () => {
  loading.value = true
  const cakeId = route.params.id

  try {
    cake.value = await cakeApi.detail(cakeId)
  } catch (e) {
    cake.value = {
      id: cakeId,
      name: '经典草莓奶油',
      description: '新鲜草莓搭配轻盈奶油，口感丝滑，每一口都是幸福的味道',
      price: 168,
      flavor: '草莓',
      sold: 520
    }
  }

  try {
    reviews.value = await reviewApi.byCake(cakeId, { page: 1, size: 5 })
  } catch (e) {
    reviews.value = [
      { id: 1, score: 5, content: '蛋糕非常好吃，客服态度也很好！', createTime: '2024-01-15' },
      { id: 2, score: 4, content: '味道不错，就是配送稍慢了一些', createTime: '2024-01-10' }
    ]
  }

  loading.value = false
})

const addToCart = async () => {
  await cartStore.addToCart({
    cakeId: cake.value.id,
    cakeName: cake.value.name,
    price: cake.value.price,
    amount: quantity.value,
    bakeryId: 1,
    size: selectedSize.value,
    blessing: blessing.value
  })
}

const buyNow = async () => {
  await addToCart()
  router.push('/cart')
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.cake-detail-page {
  padding: 32px 0 80px;
}

.el-breadcrumb {
  margin-bottom: 32px;
}

.detail-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  margin-bottom: 60px;
}

.detail-gallery {
  .main-image {
    aspect-ratio: 1;
    background: linear-gradient(135deg, $bg-warm, $secondary);
    border-radius: $radius-xl;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .cake-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: inherit;
  }

  .image-placeholder {
    font-size: 12rem;
  }
}

.detail-info {
  .detail-title {
    font-size: 2rem;
    margin-bottom: 16px;
  }

  .detail-desc {
    color: $text-light;
    line-height: 1.8;
    margin-bottom: 24px;
  }
}

.detail-price {
  display: flex;
  align-items: baseline;
  gap: 12px;
  padding: 20px 0;
  border-bottom: 1px solid $border;
  margin-bottom: 24px;

  .price-label {
    color: $text-light;
  }

  .price-value {
    font-size: 2.5rem;
    font-weight: 600;
    color: $primary;
  }
}

.detail-meta {
  display: flex;
  gap: 32px;
  margin-bottom: 32px;

  .meta-item {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .meta-label {
    font-size: 0.85rem;
    color: $text-light;
  }

  .meta-value {
    font-weight: 500;
  }
}

.customization {
  background: $bg-warm;
  border-radius: $radius-lg;
  padding: 24px;
  margin-bottom: 32px;

  h3 {
    font-size: 1.1rem;
    margin-bottom: 20px;
  }
}

.custom-field {
  margin-bottom: 20px;

  &:last-child {
    margin-bottom: 0;
  }

  label {
    display: block;
    font-size: 0.9rem;
    color: $text-light;
    margin-bottom: 8px;
  }
}

.detail-actions {
  display: flex;
  gap: 16px;
}

.btn--lg {
  padding: 14px 32px;
  font-size: 1rem;
  gap: 8px;
}

.review-section {
  .section-title {
    font-size: 1.5rem;
    margin-bottom: 24px;
  }
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-item {
  background: $white;
  border-radius: $radius-md;
  padding: 20px;
  box-shadow: $shadow-sm;
}

.review-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.review-time {
  font-size: 0.85rem;
  color: $text-light;
}

.review-content {
  color: $text-dark;
  line-height: 1.6;
}

.review-reply {
  margin-top: 12px;
  padding: 12px;
  background: $bg-warm;
  border-radius: $radius-sm;
  font-size: 0.9rem;
  color: $text-light;

  strong {
    color: $primary;
  }
}

@media (max-width: 768px) {
  .detail-content {
    grid-template-columns: 1fr;
    gap: 32px;
  }

  .detail-actions {
    flex-direction: column;
  }
}
</style>
