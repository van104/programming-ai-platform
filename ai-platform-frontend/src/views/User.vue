<template>
  <div class="user-container">
    <!-- 头部导航 -->
    <div class="header">
      <button class="nav-btn" @click="$router.push('/home')">返回首页</button>
      <button class="logout-btn" @click="logout">退出登录</button>
    </div>

    <h2>用户管理</h2>

    <!-- 数据表格 -->
    <div class="table-wrapper">
      <table class="user-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>角色</th>
            <th>创建时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in list" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>
              <span class="role-tag" :class="user.role">
                {{ user.role === 'admin' ? '管理员' : user.role === 'teacher' ? '教师' : '学生' }}
              </span>
            </td>
            <td>{{ user.createTime }}</td>
          </tr>
          <tr v-if="list.length === 0">
            <td colspan="4" class="empty-row">暂无数据</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="actions">
      <button class="refresh-btn" @click="load">刷新数据</button>
    </div>
  </div>
</template>

<script>
import { getUserList } from '@/api/user'

export default {
  data() {
    return {
      list: []
    }
  },

  async mounted() {
    await this.load()
  },

  methods: {
    async load() {
      try {
        this.list = await getUserList()
      } catch (error) {
        console.error('加载用户列表失败:', error)
      }
    },

    // 1. 清除本地存储
    // 2. 跳转到登录页面
     logout() {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('userId')
      this.$router.push('/login')
    }
  }
}
</script>

<!--<style scoped>-->
<!--.user-container {-->
<!--  display: flex;-->
<!--  flex-direction: column;-->
<!--  align-items: center;-->
<!--  min-height: 100vh;-->
<!--  padding: 0 20px;-->
<!--}-->

<!--.header {-->
<!--  position: fixed;-->
<!--  top: 20px;-->
<!--  right: 20px;-->
<!--  display: flex;-->
<!--  gap: 10px;-->
<!--}-->

<!--.nav-btn {-->
<!--  padding: 8px 16px;-->
<!--  background-color: #409eff;-->
<!--  color: #fff;-->
<!--  border: none;-->
<!--  border-radius: 4px;-->
<!--  cursor: pointer;-->
<!--  font-size: 14px;-->
<!--  transition: background-color 0.3s;-->
<!--}-->

<!--.nav-btn:hover {-->
<!--  background-color: #337ecc;-->
<!--}-->

<!--.logout-btn {-->
<!--  padding: 8px 16px;-->
<!--  background-color: #f44336;-->
<!--  color: #fff;-->
<!--  border: none;-->
<!--  border-radius: 4px;-->
<!--  cursor: pointer;-->
<!--  font-size: 14px;-->
<!--  transition: background-color 0.3s;-->
<!--}-->

<!--.logout-btn:hover {-->
<!--  background-color: #d32f2f;-->
<!--}-->

<!--h2 {-->
<!--  color: #333;-->
<!--  margin: 60px 0 20px;-->
<!--}-->

<!--.table-wrapper {-->
<!--  width: 100%;-->
<!--  max-width: 800px;-->
<!--  overflow-x: auto;-->
<!--}-->

<!--.user-table {-->
<!--  width: 100%;-->
<!--  border-collapse: collapse;-->
<!--  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);-->
<!--  border-radius: 8px;-->
<!--  overflow: hidden;-->
<!--}-->

<!--.user-table thead {-->
<!--  background-color: #409eff;-->
<!--  color: #fff;-->
<!--}-->

<!--.user-table th {-->
<!--  padding: 12px 16px;-->
<!--  text-align: left;-->
<!--  font-size: 14px;-->
<!--  font-weight: 500;-->
<!--}-->

<!--.user-table td {-->
<!--  padding: 12px 16px;-->
<!--  font-size: 14px;-->
<!--  border-bottom: 1px solid #ebeef5;-->
<!--  color: #333;-->
<!--}-->

<!--.user-table tbody tr:hover {-->
<!--  background-color: #f5f7fa;-->
<!--}-->

<!--.user-table tbody tr:last-child td {-->
<!--  border-bottom: none;-->
<!--}-->

<!--.role-tag {-->
<!--  display: inline-block;-->
<!--  padding: 3px 10px;-->
<!--  border-radius: 12px;-->
<!--  font-size: 12px;-->
<!--  font-weight: 500;-->
<!--}-->

<!--.role-tag.admin {-->
<!--  background-color: #fef0f0;-->
<!--  color: #f56c6c;-->
<!--}-->

<!--.role-tag.teacher {-->
<!--  background-color: #fdf6ec;-->
<!--  color: #e6a23c;-->
<!--}-->

<!--.role-tag.student {-->
<!--  background-color: #ecf5ff;-->
<!--  color: #409eff;-->
<!--}-->

<!--.empty-row {-->
<!--  text-align: center;-->
<!--  color: #999;-->
<!--  padding: 30px 0;-->
<!--}-->

<!--.actions {-->
<!--  margin-top: 20px;-->
<!--}-->

<!--.refresh-btn {-->
<!--  padding: 10px 24px;-->
<!--  background-color: #409eff;-->
<!--  color: #fff;-->
<!--  border: none;-->
<!--  border-radius: 4px;-->
<!--  cursor: pointer;-->
<!--  font-size: 14px;-->
<!--  transition: background-color 0.3s;-->
<!--}-->

<!--.refresh-btn:hover {-->
<!--  background-color: #337ecc;-->
<!--}-->
<!--</style>-->
