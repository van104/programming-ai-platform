<template>
  <div class="centered-container">
    <h2>{{ msg }}</h2>
    <template v-if="!msg">
      <h2>AI Programming Teaching Platform</h2>
      <button @click="loadData">测试后端接口</button>
    </template>

    <template v-else>
      <button @click="backToHome">返回首页</button>
    </template>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      msg: "",
    };
  },
  methods: {
    loadData() {
      axios
        .get("http://localhost:8080/hello")
        .then((res) => {
          this.msg = res.data;
        })
        .catch((error) => {
          console.error("请求失败:", error);
          this.msg = "连接后端接口失败，请检查后端服务是否启动。";
        });
    },

    backToHome() {
      this.msg = "";
    },
  },
};
</script>

<style scoped>
.centered-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

h2 {
  text-align: center;
  margin-bottom: 20px;
}

button {
  display: block;
  margin-top: 10px;
  padding: 8px 16px;
  cursor: pointer;
}
</style>
