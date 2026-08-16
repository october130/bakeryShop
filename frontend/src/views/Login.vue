<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-left">
        <div class="brand">
          <div class="brand-logo">
            <el-icon :size="40" color="#fff"><Food /></el-icon>
          </div>
          <h1>蛋糕烘焙坊</h1>
          <p>CAKE BAKERY</p>
          <span class="brand-slogan">用心烘焙，传递甜蜜</span>
        </div>
      </div>

      <div class="login-right">
        <div class="login-form">
          <h2>欢迎回来</h2>
          <p class="subtitle">登录您的账户</p>

          <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
            <el-form-item prop="phone">
              <el-input
                v-model="form.phone"
                placeholder="请输入手机号"
                prefix-icon="Phone"
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
                @keyup.enter="handleLogin"
              />
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                class="login-btn"
                @click="handleLogin"
                :loading="loading"
              >
                登录
              </el-button>
            </el-form-item>
          </el-form>

          <p class="register-link">
            还没有账号？
            <router-link to="/register">立即注册</router-link>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)

// 后端 LoginDTO: { phone, password }
const form = reactive({
  phone: '',
  password: ''
})

const rules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await userStore.login(form)
    ElMessage.success('登录成功')
    const redirect = route.query.redirect || '/'
    router.push(redirect)
  } catch (e) {
    ElMessage.error('登录失败，请检查手机号和密码')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
@use '@/styles/variables' as *;

.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-light;
  padding: 20px;
}

.login-container {
  display: flex;
  width: 100%;
  max-width: 900px;
  min-height: 500px;
  background: $white;
  border-radius: $radius-lg;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, $primary 0%, $primary-dark 100%);
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

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.login-form {
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

  .login-btn {
    width: 100%;
    height: 44px;
    font-size: 15px;
  }
}

.register-link {
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
  .login-container {
    flex-direction: column;
    max-width: 400px;
    min-height: auto;
  }

  .login-left {
    padding: 32px 20px;
  }
}
</style>
