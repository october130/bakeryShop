<template>
  <div class="review-page">
    <div class="container">
      <el-page-header @back="$router.back()" title="返回" />

      <div class="review-content">
        <h1>订单评价</h1>
        <p class="order-info">订单号：{{ orderId }}</p>

        <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
          <el-form-item label="评分" prop="score">
            <el-rate v-model="form.score" :texts="['很差', '较差', '一般', '满意', '非常满意']" show-text />
          </el-form-item>

          <el-form-item label="评价内容" prop="content">
            <el-input
              v-model="form.content"
              type="textarea"
              :rows="5"
              placeholder="请分享您的品尝体验..."
              maxlength="500"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="上传图片">
            <el-upload
              action="#"
              list-type="picture-card"
              :auto-upload="false"
              :limit="3"
              accept="image/*"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
            <p class="upload-tip">最多上传3张图片</p>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="submitReview" :loading="submitting">
              提交评价
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { reviewApi } from '@/api/modules'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const orderId = route.params.orderId
const formRef = ref(null)
const submitting = ref(false)

const form = reactive({
  orderId: parseInt(orderId),
  score: 5,
  content: '',
  images: ''
})

const rules = {
  score: [{ required: true, message: '请选择评分' }],
  content: [{ required: true, message: '请输入评价内容' }]
}

const submitReview = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await reviewApi.create(form)
    ElMessage.success('评价成功')
    router.push('/orders')
  } catch (e) {
    ElMessage.error('评价失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.review-page {
  padding: 32px 0 80px;
}

.el-page-header {
  margin-bottom: 32px;
}

.review-content {
  max-width: 600px;
  margin: 0 auto;
  background: $white;
  border-radius: $radius-xl;
  padding: 40px;
  box-shadow: $shadow-md;

  h1 {
    font-size: 1.8rem;
    margin-bottom: 8px;
  }

  .order-info {
    color: $text-light;
    margin-bottom: 32px;
  }
}

.upload-tip {
  font-size: 0.8rem;
  color: $text-light;
  margin-top: 8px;
}
</style>
