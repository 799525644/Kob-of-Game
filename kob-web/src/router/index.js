import { createRouter, createWebHistory } from 'vue-router'

import PkIndexView from "@/views/pk/PkIndexView"
import RecordIndexView from "@/views/record/RecordIndexView"
import RanklistIndexView from "@/views/ranklist/RanklistIndexView"
import UserBotIndexView from "@/views/user/bot/UserBotIndexView"
import NotFund from "@/views/error/NotFound"

import UserAccountLoginView  from '@/views/user/account/UserAccountLoginView'
import UserAccountRegisterView  from '@/views/user/account/UserAccountRegisterView'
import store from '@/store/index'

const routes = [
  {
    path: "/",
    name: "home",
    redirect: "/pk/",
    meta:{
      requestAuth: true, 
    }
  },
  {
    path: "/pk/",
    name: "pk_index",
    component: PkIndexView,
    meta:{
      requestAuth: true,
    }
  },
  {
    path:"/user/account/login/",
    name:"user_account_login",
    component: UserAccountLoginView,
  },
  {
    path:"/user/account/register/",
    name:"user_account_register",
    component: UserAccountRegisterView,
  },
  {
    path: "/record/",
    name: "record_index",
    component: RecordIndexView,
    meta:{
      requestAuth: true,
    }
  },
  {
    path: "/ranklist/",
    name: "ranklist_index",
    component: RanklistIndexView,
    meta:{
      requestAuth: true,
    }
  },
  {
    path: "/user/bot/",
    name: "user_bot_index",
    component: UserBotIndexView,
    meta:{
      requestAuth: true,
    }
  },
  {
    path: "/404/",
    name: "404",
    component: NotFund
  },
  {
    path: "/:catchAll(.*)",
    redirect: "/404/"
  },]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) =>{
  if(to.meta.requestAuth && !store.state.user.is_login){
    next({name:"user_account_login"}); // 表示未登陆，则重定向到登录页面
  }else{
    next(); //表示如果登陆后，进入默认页面
  }
})
export default router
