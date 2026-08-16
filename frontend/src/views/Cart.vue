<template>
  <div class="cart-page">
    <div class="page-header container">
      <h1 class="page-header__title">购物车</h1>
    </div>

    <div class="container">
      <div class="cart-content" v-loading="cartStore.loading">
        <div class="cart-items" v-if="cartStore.items.length > 0">
          <div class="cart-item" v-for="item in cartStore.items" :key="item.cakeId">
            <div class="item-image">
              <img :src="item.image || defaultImage" :alt="item.cakeName" @error="(e) => e.target.src = defaultImage" class="item-img" />
            </div>
            <div class="item-info">
              <h4 class="item-name">{{ item.cakeName }}</h4>
              <p class="item-spec" v-if="item.size || item.blessing">
                <span v-if="item.size">尺寸：{{ item.size }}寸</span>
                <span v-if="item.blessing">祝福语：{{ item.blessing }}</span>
              </p>
              <div class="item-price">¥{{ (item.price / 100).toFixed(0) }}</div>
            </div>
            <div class="item-actions">
              <el-input-number
                v-model="item.amount"
                :min="1"
                :max="10"
                size="small"
                @change="updateAmount(item)"
              />
              <el-button type="danger" text @click="removeItem(item.cakeId)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </div>
          </div>
        </div>

        <el-empty v-else description="购物车空空如也~">
          <router-link to="/cakes" class="btn btn--primary">去逛逛</router-link>
        </el-empty>

        <div class="cart-footer" v-if="cartStore.items.length > 0">
          <div class="footer-info">
            <span class="total-label">合计：</span>
            <span class="total-price">¥{{ cartStore.totalPrice }}</span>
          </div>
          <div class="footer-actions">
            <el-button @click="cartStore.clearCart()">清空购物车</el-button>
            <el-button type="primary" size="large" @click="showCheckout = true">
              去结算
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 结算弹窗 -->
    <el-dialog v-model="showCheckout" title="确认订单" width="500px">
      <el-form :model="orderForm" label-width="80px">
        <el-form-item label="收货地址">
          <el-input v-model="orderForm.address" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="orderForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="配送时间">
          <el-date-picker
            v-model="orderForm.deliverTime"
            type="datetime"
            placeholder="选择配送时间"
            format="YYYY-MM-DD HH:mm"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="orderForm.remark" type="textarea" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCheckout = false">取消</el-button>
        <el-button type="primary" @click="submitOrder">提交订单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { orderApi } from '@/api/modules'
import { ElMessage } from 'element-plus'

const router = useRouter()
const cartStore = useCartStore()
const defaultImage = 'https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400&h=400&fit=crop'

const showCheckout = ref(false)
const orderForm = ref({
  address: '',
  phone: '',
  deliverTime: '',
  remark: ''
})

const updateAmount = (item) => {
  cartStore.updateItem({
    cakeId: item.cakeId,
    amount: item.amount
  })
}

const removeItem = (cakeId) => {
  cartStore.removeItem(cakeId)
}

const submitOrder = async () => {
  if (!orderForm.value.address || !orderForm.value.phone) {
    ElMessage.warning('请填写完整的收货信息')
    return
  }

  try {
    const result = await orderApi.create(orderForm.value)
    ElMessage.success('下单成功')
    await cartStore.clearCart()
    showCheckout.value = false
    router.push(`/order/${result.id}`)
  } catch (e) {
    ElMessage.error('下单失败')
  }
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.cart-page {
  padding-bottom: 80px;
}

.cart-content {
  max-width: 900px;
  margin: 0 auto;
}

.cart-items {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 32px;
}

.cart-item {
  display: flex;
  align-items: center;
  gap: 20px;
  background: $white;
  border-radius: $radius-lg;
  padding: 20px;
  box-shadow: $shadow-sm;
}

.item-image {
  width: 100px;
  height: 100px;
  background: $bg-warm;
  border-radius: $radius-md;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.item-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: inherit;
}

.item-placeholder {
  font-size: 3rem;
}

.item-info {
  flex: 1;

  .item-name {
    font-size: 1.1rem;
    margin-bottom: 8px;
  }

  .item-spec {
    font-size: 0.85rem;
    color: $text-light;
    margin-bottom: 8px;

    span + span {
      margin-left: 16px;
    }
  }

  .item-price {
    font-size: 1.2rem;
    font-weight: 600;
    color: $primary;
  }
}

.item-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}

.cart-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: $white;
  border-radius: $radius-lg;
  padding: 24px;
  box-shadow: $shadow-sm;
}

.footer-info {
  .total-label {
    font-size: 1rem;
    color: $text-light;
  }

  .total-price {
    font-size: 1.8rem;
    font-weight: 600;
    color: $primary;
  }
}

.footer-actions {
  display: flex;
  gap: 12px;
}

@media (max-width: 640px) {
  .cart-item {
    flex-direction: column;
    text-align: center;
  }

  .item-actions {
    flex-direction: row;
    align-items: center;
  }

  .cart-footer {
    flex-direction: column;
    gap: 20px;
  }
}
</style>
