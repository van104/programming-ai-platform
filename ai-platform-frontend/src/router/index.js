import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Home from '@/views/Home.vue'
import AiChat from '@/views/AiChat.vue'
import Stat from '@/views/Stat.vue'

//路由守卫的作用：在页面跳转之前检查是否有 token，没有就踢回登录页。
const routes = [
  {
    path: '/',
    redirect: '/login' // 根路径自动跳到登录页
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/home',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true } // 标记：此页面需要登录
  },
  {
    path: '/ai-chat',
    name: 'AiChat',
    component: AiChat,
    meta: { requiresAuth: true }
  },
  {
    path: '/stat',
    name: 'Stat',
    component: Stat,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// ========== 全局前置守卫 ==========
/*
 * to: 要跳转的页面
 * from: 从哪个页面跳转过来的
 * next: 放行函数
 */
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  // 情况1：要访问需要登录的页面，但没有 token → 踢回登录页
  if (to.meta.requiresAuth && !token) {
    next('/login')
  }
  // 情况2：已登录，却想去登录页 → 直接跳到首页
  else if (to.path === '/login' && token) {
    next('/home')
  }
  // 情况3：其他情况正常放行
  else {
    next()
  }
})

export default router
