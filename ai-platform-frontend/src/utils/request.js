import axios from 'axios'
import router from '@/router'

const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000
})
// ========== 请求拦截器 ==========
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, error => {
  return Promise.reject(error)
})

// ========== 响应拦截器 ==========
request.interceptors.response.use(response => {
  const body = response.data
  // 后端返回的 code 是 401，说明 token 过期或无效
  if (body.code === 401) {
    localStorage.removeItem('token')
    router.push('/login')
    return Promise.reject(new Error(body.message || '未登录'))
  }
  // 正常情况：直接返回 data 字段，组件不用关心 code 和 message
  return body.data
}, error => {
  // HTTP 状态码就是 401（请求根本没到 Controller，被拦截器挡了）
  if (error.response && error.response.status === 401) {
    localStorage.removeItem('token')
    router.push('/login')
  }
  return Promise.reject(error)
})

export default request
