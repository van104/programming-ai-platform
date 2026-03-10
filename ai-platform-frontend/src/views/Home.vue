<template>
  <div class="centered-container">
    <h2>{{ msg }}</h2>
    <template v-if="!msg">
      <h2>融合大语言模型的高校编程教学智能支持平台</h2>
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
  name: "Home",
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
          this.msg = res.data.data !== undefined ? res.data.data : res.data;
        })
        .catch((err) => {
          this.msg = "后端接口连接失败";
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
