<template>
  <div class="bakery-page">
    <div class="page-header container">
      <h1 class="page-header__title">附近门店</h1>
      <p class="page-header__subtitle">新鲜美味，就在身边</p>
    </div>

    <div class="container">
      <div class="bakery-grid">
        <div class="bakery-card" v-for="bakery in bakeries" :key="bakery.id">
          <div class="bakery-image">
            <div class="image-placeholder">🏪</div>
            <span class="distance" v-if="bakery.distance">{{ bakery.distance }}km</span>
          </div>
          <div class="bakery-info">
            <h3 class="bakery-name">{{ bakery.name }}</h3>
            <p class="bakery-address">
              <el-icon><Location /></el-icon>
              {{ bakery.address }}
            </p>
            <p class="bakery-hours">
              <el-icon><Clock /></el-icon>
              {{ bakery.openTime }} - {{ bakery.closeTime }}
            </p>
            <div class="bakery-footer">
              <el-rate v-model="bakery.rating" disabled show-score text-color="#8B2635" />
              <el-button type="primary" size="small" @click="$router.push('/cakes')">
                去选购
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <el-empty v-if="bakeries.length === 0" description="暂无附近门店" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { bakeryApi } from '@/api/modules'

const bakeries = ref([])

onMounted(async () => {
  try {
    bakeries.value = await bakeryApi.list()
  } catch (e) {
    // 模拟数据
    bakeries.value = [
      {
        id: 1,
        name: '甜蜜时光·国贸店',
        address: '北京市朝阳区国贸大厦B1层',
        openTime: '09:00',
        closeTime: '21:00',
        distance: 1.2,
        rating: 4.8
      },
      {
        id: 2,
        name: '甜蜜时光·三里屯店',
        address: '北京市朝阳区三里屯太古里南区',
        openTime: '10:00',
        closeTime: '22:00',
        distance: 2.5,
        rating: 4.9
      },
      {
        id: 3,
        name: '甜蜜时光·望京店',
        address: '北京市朝阳区望京SOHO T1',
        openTime: '08:30',
        closeTime: '20:30',
        distance: 5.8,
        rating: 4.7
      }
    ]
  }
})
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.bakery-page {
  padding-bottom: 80px;
}

.bakery-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.bakery-card {
  background: $white;
  border-radius: $radius-lg;
  overflow: hidden;
  box-shadow: $shadow-sm;
  transition: $transition;

  &:hover {
    transform: translateY(-8px);
    box-shadow: $shadow-lg;
  }
}

.bakery-image {
  position: relative;
  height: 180px;
  background: linear-gradient(135deg, $bg-warm, $secondary);
  display: flex;
  align-items: center;
  justify-content: center;

  .image-placeholder {
    font-size: 5rem;
  }

  .distance {
    position: absolute;
    top: 12px;
    right: 12px;
    padding: 4px 12px;
    background: $primary;
    color: $white;
    font-size: 0.8rem;
    font-weight: 600;
    border-radius: 20px;
  }
}

.bakery-info {
  padding: 20px;

  .bakery-name {
    font-size: 1.2rem;
    margin-bottom: 12px;
  }

  .bakery-address,
  .bakery-hours {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 0.9rem;
    color: $text-light;
    margin-bottom: 8px;
  }

  .bakery-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-top: 16px;
    padding-top: 16px;
    border-top: 1px solid $border;
  }
}

@media (max-width: 768px) {
  .bakery-grid {
    grid-template-columns: 1fr;
  }
}
</style>
