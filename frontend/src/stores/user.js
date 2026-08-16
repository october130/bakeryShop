import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api/modules'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  const isLoggedIn = computed(() => !!token.value)

  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  const setUserInfo = (info) => {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  // 后端 LoginDTO: { phone, password }
  // 后端返回: { token, userInfo: { id, phone, nickname, avatar, role } }
  const login = async (loginData) => {
    const data = await userApi.login({
      phone: loginData.phone,
      password: loginData.password
    })
    setToken(data.token)
    setUserInfo(data.userInfo || data)
    return data
  }

  // 后端 RegisterDTO: { phone, password, nickname }
  const register = async (registerData) => {
    return await userApi.register({
      phone: registerData.phone,
      password: registerData.password,
      nickname: registerData.nickname || registerData.username
    })
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    login,
    register,
    logout,
    setToken,
    setUserInfo
  }
})
