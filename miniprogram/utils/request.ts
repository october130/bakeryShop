// 统一的请求封装：自动带 token、统一处理后端 Result 包装
import { BASE_URL } from '../config'

interface ReqOption {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: Record<string, any>
  needToken?: boolean // 默认 true
}

export function request<T = any>(opt: ReqOption): Promise<T> {
  return new Promise((resolve, reject) => {
    const token = wx.getStorageSync('token')
    wx.request({
      url: BASE_URL + opt.url,
      method: opt.method || 'GET',
      data: opt.data,
      header: {
        'Content-Type': 'application/json',
        ...(opt.needToken === false || !token ? {} : { Authorization: 'Bearer ' + token }),
      },
      success(res) {
        const body = res.data as any
        if (res.statusCode >= 200 && res.statusCode < 300 && body && body.code === 200) {
          resolve(body.data as T)
        } else {
          reject(new Error((body && body.message) || ('请求失败(' + res.statusCode + ')')))
        }
      },
      fail(err) {
        reject(new Error(err.errMsg || '网络错误'))
      },
    })
  })
}
