<!-- 
  **PK模块->背景PlayGround->游戏面板GameMap|匹配面板MatchGround
-->
<template lang="">
    <PlayGround v-if="$store.state.pk.status === 'playing'" />
    <MatchGround v-if="$store.state.pk.status === 'matching'" />
    <ResultBoard v-if="$store.state.pk.loser != 'none'"></ResultBoard>
</template>

<script>
import PlayGround from '../../components/PlayGround.vue'
import MatchGround from '../../components/MatchGround.vue'
import { onMounted, onUnmounted} from 'vue'
import { useStore} from 'vuex'
import ResultBoard from '@/components/ResultBoard.vue'

export default {
    components:{
      PlayGround,
      MatchGround,
      ResultBoard
    },
    setup(){
      const store = useStore();
      // 自定义ws变量socket
      let socket = null;
      const socketUrl = `ws://127.0.0.1:3001/websocket/${store.state.user.token}/`; // ws协议格式，注意当字符串中有模版字符串，需要用反引号，是 ` 不是 '


      // 当前组件onMounted时，显示占位的对手的名称和头像
      onMounted(()=>{
        store.commit("updateOpponent",{
          username:"我的对手",
          photo:"https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
        })
        // 赋值socket，及其函数onopen、onmessage（不同后端的框架定义方式不同如onmessage的msg）
        socket = new WebSocket(socketUrl);
        socket.onopen = () =>{
          console.log("socket connected!");
          store.commit("updateSocket", socket);
        }
        socket.onmessage = msg =>{
          const data = JSON.parse(msg.data);
          if(data.event === "start-matching"){
            console.log("start-matching received:"+data);
            store.commit("updateOpponent",{
              username: data.opponent_username,
              photo: data.opponent_photo,
            });
            // 延迟后跳转到playing某个页面
            setTimeout(()=>{
              store.commit("updateStatus", "playing");
            },1000);
            store.commit("updateGame", data.game); // 更新game数据
          }else if(data.event === "move"){ 
            console.log("move received:"+data);
            const game = store.state.pk.gameObject;
            const [snake0, snake1] = game.snakes; // 解构出来
            snake0.set_direction(data.a_direction);
            snake1.set_direction(data.b_direction);
          }else if(data.event === "result"){
            console.log("result received"+data);
            const game = store.state.pk.gameObject;
            const [snake0, snake1] = game.snakes;
            if (data.loser === "all" || data.loser === "A") {
                snake0.status = "die";
            }
            if (data.loser === "all" || data.loser === "B") {
                snake1.status = "die";
            }
            store.commit("updateLoser", data.loser);
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
  



 