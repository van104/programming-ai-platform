import axios from 'axios'

// 创建 axios 实例
const request = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 5000
})

/**
 * 请求拦截器
 * 作用：自动携带 Token
 */
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.token = token
  }
  return config
}, error => {
  return Promise.reject(error)
})

/**
 * 响应拦截器
 * 作用：统一处理返回数据
 */
request.interceptors.response.use(response => {
  // 后端返回结构：
  // { code:200, msg:'success', data:xxx }
  return response.data.data
}, error => {
  alert('请求失败，请检查服务器')
  return Promise.reject(error)
})

export default request
