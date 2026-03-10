<template>
  <div>
    <h2>{{ msg }}</h2>
    <template v-if="!msg">
      <h2>AI Programming Teaching Platform</h2>
    </template>
    <button @click="loadData">测试后端接口</button>
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
      axios.get("http://localhost:8080/hello").then((res) => {
        this.msg = res.data;
      });
    },
  },
};
</script>
