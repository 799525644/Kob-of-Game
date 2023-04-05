<template>
  <nav-bar></nav-bar>
  <router-view />
</template>
<script>
import NavBar from './components/NavBar.vue';
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"
// import store from './store';
import { useStore } from 'vuex'
import router from './router';

// import $ from 'jquery'
export default {
  name: "App",
  components: {
    NavBar
  },
  // 一般建议在App.vue中获取token
  setup() {
    const store = useStore();
    const jwt_token = localStorage.getItem("jwt_token");
      if (jwt_token) {
        store.commit("updateToken", jwt_token)
        store.dispatch("getinfo", {
          success() {
            router.push({name:"home"});
            store.commit("updatePullingInfo", false);
            console.log("state:", store.state);
          },
          error() {
            store.commit("updatePullingInfo", false);
          }
        });
      } else {
        store.commit("updatePullingInfo", false);
      }
  },




}
</script>
<style>
body {
  /* 1. 图片路径 */
  background-image: url("@/assets/images/bkg.png");
  /* 2. 填充效果 cover｜n% m% */
  background-size: 100% 100%;
  /* 3. ★配合100% 100% fixed实现拉伸 */
  background-attachment: fixed;
}
</style>

