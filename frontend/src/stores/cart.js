import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { cartApi } from '@/api/modules'
import { ElMessage } from 'element-plus'

export const useCartStore = defineStore('cart', () => {
  const items = ref([])
  const loading = ref(false)

  const totalCount = computed(() =>
    items.value.reduce((sum, item) => sum + (item.amount || 0), 0)
  )

  const totalPrice = computed(() =>
    items.value.reduce((sum, item) => sum + (item.price || 0) * (item.amount || 0), 0) / 100
  )

  const fetchCart = async () => {
    loading.value = true
    try {
      const data = await cartApi.list()
      items.value = data || []
    } catch (e) {
      items.value = []
    } finally {
      loading.value = false
    }
  }

  // 后端 CartAddDTO: { cakeId, amount }
  const addToCart = async (item) => {
    try {
      await cartApi.add({
        cakeId: item.cakeId,
        amount: item.amount || 1
      })
      await fetchCart()
      ElMessage.success('已加入购物车')
    } catch (e) {
      ElMessage.error('加入失败')
    }
  }

  const removeItem = async (cakeId) => {
    try {
      await cartApi.remove(cakeId)
      await fetchCart()
      ElMessage.success('已移除')
    } catch (e) {
      ElMessage.error('移除失败')
    }
  }

  const updateItem = async ({ cakeId, amount }) => {
    try {
      const current = items.value.find(i => i.cakeId === cakeId)
      const delta = amount - (current?.amount || 0)
      if (delta === 0) return
      await cartApi.add({ cakeId, amount: delta })
      await fetchCart()
    } catch (e) {
      ElMessage.error('更新失败')
    }
  }

  const clearCart = async () => {
    try {
      for (const item of items.value) {
        await cartApi.remove(item.cakeId)
      }
      items.value = []
    } catch (e) {
      ElMessage.error('清空失败')
    }
  }

  return {
    items,
    loading,
    totalCount,
    totalPrice,
    fetchCart,
    addToCart,
    removeItem,
    updateItem,
    clearCart
  }
})
