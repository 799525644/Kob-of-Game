<!-- 
  ->>PK模块
        背景PlayGround
            游戏面板GameMap
-->
<template lang="">
    <PlayGround v-if="$store.state.pk.status === 'playing'" />
    <MatchGround v-if="$store.state.pk.status === 'matching'" />
</template>

<script>
import PlayGround from '../../components/PlayGround.vue'
import MatchGround from '../../components/MatchGround.vue'
import { onMounted, onUnmounted} from 'vue'
import { useStore} from 'vuex'

export default {
    components:{
      PlayGround,
      MatchGround
    },
    setup(){
      const store = useStore();
      // ws协议格式
      const socketUrl = `ws://127.0.0.1:3001/websocket/${store.state.user.token}/`; // 字符串中如果有模版字符串，需要用反引号，是 ` 不是 '
      // 声明socket，再onMounted的时候初始化
      let socket = null;

      // 当前组件onMounted（挂载完成后执行）时
      // 设置默认的对手名称和头像
      onMounted(()=>{
        store.commit("updateOpponent",{
          username:"我的对手",
          photo:"https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
        })
        // 自定义前端socket，及其函数onopen、onmessage（不同后端的框架定义方式不同如onmessage的msg）
        socket = new WebSocket(socketUrl);

        socket.onopen = () =>{
          console.log("connected!");
          store.commit("updateSocket", socket);
        }
        socket.onmessage = msg =>{
          const data = JSON.parse(msg.data);
          if(data.event === "start-matching"){
            store.commit("updateOpponent",{
              username: data.opponent_username,
              photo: data.opponent_photo,
            });
            // 延迟后跳转到某个页面
            setTimeout(()=>{
              store.commit("updateStatus","playing");
            },2000);
            store.commit("updateGamemap",data.gamemap); //更新地图
          }
        }

        socket.onclose = () =>{
          console.log("disconnected!");
        }
      });

      // 当前组件unmounted（卸载完成后执行）时
      onUnmounted(() => {
        socket.close();// 客户端必须发起关闭链接请求，不然后端将很容易的持续冗余连接
        store.commit("updateStatus", "matching"); // 断开后需要回到匹配页面
      })
    }
}
</script>
<style scoped lang="">

</style>
  



 