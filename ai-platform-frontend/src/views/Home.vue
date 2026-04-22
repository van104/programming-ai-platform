<template>
  <div class="container">
    <h2>用户列表测试</h2>
    <button @click="load">加载用户</button>
    <div v-for="item in list" :key="item.id" class="user-item">
      {{ item.id }}: {{ item.username }}
    </div>


    <input
      v-model="deleteId"
      placeholder="输入ID删除用户"
      style="margin: 10px 0; padding: 5px; width: 180px"
    />
    <button @click="delUserById(deleteId)">删除用户</button>

    <hr />
    <h2>获取用户数据测试</h2>
    <input
      v-model="userId"
      placeholder="输入用户ID"
      style="margin: 10px 0; padding: 5px; width: 180px"
    />
    <button @click="getUserData(userId)">获取用户数据</button>
    <div v-if="userData">
      <h3>用户画像</h3>
      <p>AI 使用次数: {{ userData.profile.aiUsageCount }}</p>
      <p>提交次数: {{ userData.profile.submissionCount }}</p>
      <p>活跃度: {{ userData.profile.activityLevel }}</p>
    </div>
  </div>
</template>

<script>
import { getUserList, delUserById  } from "@/api/user";
import { getUserData as apiGetUserData } from "@/api/learningprofile";

export default {
  data() {
    return {
      list: [],
      deleteId: "",
      userId: "",
      userData: null,
    };
  },

  methods: {
    async load() {
      try {
        this.list = await getUserList();
      } catch (error) {
        console.error("加载用户失败:", error);
      }
    },

    async delUserById(id) {
      if (!id) {
        alert('请先输入要删除的用户ID');
        return;
      }
      try {
        await delUserById(id);
        alert('删除成功');
        this.deleteId = '';
        this.list = await getUserList();
      } catch (error) {
        console.error('删除失败:', error);
        alert('删除失败，请检查ID是否正确');
      }
    },

    async getUserData(id) {
      if (!id) {
        alert('请先输入要查询的用户ID');
        return;
      }
      try {
        this.userData = await apiGetUserData(id);
      } catch (error) {
        console.error('获取用户数据失败:', error);
        alert('获取用户数据失败，请检查ID是否正确');
      }
    },
  },
};
</script>

<style scoped>
.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  text-align: center;
}

.user-item {
  margin: 5px 0;
  padding: 10px;
  background: #f9f9f9;
  border-radius: 4px;
  width: 200px;
}

h2 {
  color: #333;
}

button {
  padding: 10px 20px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-bottom: 20px;
  transition: background-color 0.3s;
}

button:hover {
  background-color: #45a049;
}
</style>
