<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-left">
        <div class="brand">
          <div class="brand-logo">
            <el-icon :size="40" color="#fff"><Food /></el-icon>
          </div>
          <h1>蛋糕烘焙坊</h1>
          <p>CAKE BAKERY</p>
          <span class="brand-slogan">开启您的甜蜜之旅</span>
        </div>
      </div>

      <div class="register-right">
        <div class="register-form">
          <h2>创建账户</h2>
          <p class="subtitle">注册成为会员</p>

          <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleRegister">
            <el-form-item prop="phone">
              <el-input
                v-model="form.phone"
                placeholder="请输入手机号"
                prefix-icon="Phone"
                size="large"
              />
            </el-form-item>

            <el-form-item prop="nickname">
              <el-input
                v-model="form.nickname"
                placeholder="请输入昵称"
                prefix-icon="User"
                size="large"
              />
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                size="large"
                show-password
              />
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                v-model="form.confirmPassword"
                type="password"
                placeholder="请确认密码"
                prefix-icon="Lock"
                size="large"
                show-password
                @keyup.enter="handleRegister"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="register-btn"
                @click="handleRegister"
                :loading="loading"
              >
                注册
              </el-button>
            </el-form-item>
          </el-form>

          <p class="login-link">
            已有账号？
            <router-link to="/login">立即登录</router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

// 后端 RegisterDTO: { phone, password, nickname }
const form = reactive({
  phone: '',
  nickname: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    ElMessage.error('注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-light;
  padding: 20px;
}

.register-container {
  display: flex;
  width: 100%;
  max-width: 900px;
  min-height: 550px;
  background: $white;
  border-radius: $radius-lg;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.register-left {
  flex: 1;
  background: linear-gradient(135deg, $gold 0%, #B8922E 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;

  .brand {
    text-align: center;
    color: $white;
  }

  .brand-logo {
    width: 72px;
    height: 72px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 20px;
  }

  h1 {
    font-size: 24px;
    margin-bottom: 4px;
  }

  > .brand > p {
    font-size: 12px;
    letter-spacing: 3px;
    opacity: 0.8;
    margin-bottom: 24px;
  }

  .brand-slogan {
    font-size: 14px;
    opacity: 0.9;
  }
}

.register-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.register-form {
  width: 100%;
  max-width: 340px;

  h2 {
    font-size: 24px;
    margin-bottom: 8px;
  }

  .subtitle {
    color: $text-light;
    font-size: 14px;
    margin-bottom: 32px;
  }

  .register-btn {
    width: 100%;
    height: 44px;
    font-size: 15px;
  }
}

.login-link {
  text-align: center;
  margin-top: 24px;
  color: $text-light;
  font-size: 14px;

  a {
    color: $primary;
    font-weight: 500;
  }
}

@media (max-width: 768px) {
  .register-container {
    flex-direction: column;
    max-width: 400px;
  }

  .register-left {
    display: none;
  }
}
</style>
