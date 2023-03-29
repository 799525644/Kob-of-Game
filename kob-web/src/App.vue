<template>
  <nav-bar></nav-bar>
  <router-view/>
</template>
<script>
import NavBar from './components/NavBar.vue';
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"
// import $ from 'jquery'

export default{
  name: "App",
  components:{
    NavBar
  },
  // 获取token，一般用post，url、type➕data
  setup(){


    // 注册，一般用post，url、type➕data
    // $.ajax({
    //   url:"http://127.0.0.1:3000/user/account/register/",
    //   type:"post",
    //   data:{
    //     username:"jue",
    //     password:"pjue",
    //     confirmedPassword:"pjue"
    //   },
    //   success(resp){
    //      console.log(resp);
    //   },
    //   error(resp){
    //     console.log(resp);
    //   }
    // })

  }

}
</script>
<style>
  body{
    /* 1. 图片路径 */
    background-image: url("@/assets/images/bkg.png");
    /* 2. 填充效果 cover｜n% m% */
    background-size: 100% 100%;
    /* 3. ★配合100% 100% fixed实现拉伸 */
    background-attachment: fixed;
  }
</style>

