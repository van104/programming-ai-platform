<template>
  <div>
    <h2>用户列表测试</h2>
    <button @click="load">加载用户</button>
    <div v-for="item in list" :key="item.id">
      {{ item.username }}
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

  methods: {
    async load() {
      try {
        this.list = await getUserList()
      } catch (error) {
        console.error('加载用户失败:', error)
      }
    }
  }
}
</script>

<style scoped>
h2 {
  color: #333;
}
button {
  padding: 10px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-bottom: 20px;
}
button:hover {
  background-color: #45a049;
}
</style>
