import axios from 'axios'
import router from '@/router'
import { useAuth } from '@/stores/auth'

// 前台专用请求实例：
// - 自动携带 headers.token（与你后端一致）
// - 未登录时跳转到 /front/login
const service = axios.create({
  baseURL: 'http://localhost:9601',
  timeout: 10000,
  withCredentials: true,
})

service.interceptors.request.use(
  config => {
    const auth = useAuth()
    if (auth.token) {
      config.headers = config.headers || {}
      config.headers.token = auth.token
    }
    return config
  },
  error => Promise.reject(error)
)

service.interceptors.response.use(
  response => {
    const res = response.data
    // TODO(对接后端)：如果你们未登录统一返回 code=208，前台这里跳到 /login
    if (res && res.code == 208) {
      const redirect = encodeURIComponent(router.currentRoute.value.fullPath || '/index')
      router.push(`/login?redirect=${redirect}`)
      return Promise.reject(new Error(res.message || '未登录'))
    }
    return res
  },
  error => {
    // 401 也认为未登录
    if (error.response && error.response.status === 401) {
      const redirect = encodeURIComponent(router.currentRoute.value.fullPath || '/index')
      router.push(`/login?redirect=${redirect}`)
    }
    return Promise.reject(error)
  }
)

export default service

