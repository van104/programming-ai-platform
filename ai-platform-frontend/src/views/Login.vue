<template>
  <div class="login-container">
    <div class="login-box">
      <h2>系统登录</h2>
      <div class="form-item">
        <label>用户名：</label>
        <input v-model="username" placeholder="请输入用户名">
      </div>
      <div class="form-item">
        <label>密码：</label>
        <input type="password" v-model="password" placeholder="请输入密码">
      </div>
      <button class="login-btn" @click="login">
        登录
      </button>
    </div>
  </div>
</template>

<script>
import { loginApi } from '@/api/login'
// import { login1Api, login2Api } from '@/api/login'

export default {
  data() {
    return {
      username: '',
      password: ''
    }
  },
  methods: {
    async login() {
      if (!this.username.trim()) {
        alert("请输入用户名")
        return
      }
      if (!this.password.trim()) {
        alert("请输入密码")
        return
      }
      
      try {
        // 调用登录接口
        const data = await loginApi(this.username, this.password)

        // 后端返回的 data 结构：{ id, username, token }
        if (!data || !data.token) {
          alert('用户名或密码错误')
          return
        }
        localStorage.setItem('token', data.token)
        localStorage.setItem('username', data.username)
        localStorage.setItem('userId', data.id)
        this.$router.push('/home')
        // 跳转到首页
      } catch (error) {
        alert(error.message || '登录失败')
      }
    },
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100%;
}

.login-box {
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 40px;
  width: 360px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.login-box h2 {
  margin-bottom: 24px;
  color: #333;
}

.form-item {
  margin-bottom: 16px;
  text-align: left;
}

.form-item label {
  display: block;
  margin-bottom: 4px;
  color: #555;
  font-size: 14px;
}

.form-item input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
  outline: none;
  transition: border-color 0.3s;
}

.form-item input:focus {
  border-color: #409eff;
}

.login-btn {
  width: 100%;
  padding: 10px;
  background-color: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  margin-top: 8px;
  transition: background-color 0.3s;
}

.login-btn:hover {
  background-color: #337ecc;
}

.login1-btn {
  background-color: #67c23a;
}

.login1-btn:hover {
  background-color: #529b2e;
}

.login2-btn {
  background-color: #e6a23c;
}

.login2-btn:hover {
  background-color: #cf9236;
}
</style>