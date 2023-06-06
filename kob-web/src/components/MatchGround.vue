<!-- 
  PK模块->背景PlayGround->游戏面板GameMap|**匹配面板MatchGround
-->
<template>
    <div class="matchground">
        <div class="row">
            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.user.photo" alt="">
                </div>
                <div class="user-username">
                    {{ $store.state.user.username }}
                </div>
            </div>
            <div class="col-4">
                <div class="user-select-bot">
                    <select v-model="select_bot" class="form-select" aria-label="Default select example">
                        <option value="-1" selected>亲自出马</option>
                        <option v-for="bot in bots"  :key="bot.id" :value="bot.id">{{ bot.title }}</option>
                    </select>
                </div>
            </div>
            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.pk.opponent_photo" alt="">
                </div>
                <div class="user-username">
                    {{ $store.state.pk.opponent_username }}
                </div>
            </div>
            <div class="col-12" style="text-align: center; padding-top: 15vh;">
                <button @click="click_match_btn" type="button" class="btn btn-warning btn-lg">{{ match_btn_info }}</button>
            </div>
        </div>
    </div>
</template>

<script>
import { ref } from 'vue'
import { useStore } from 'vuex';
import $ from 'jquery';

export default {
    setup() {
        const store = useStore();
        let match_btn_info = ref("开始匹配");
        let bots = ref([]);
        let select_bot = ref(-1);

        const click_match_btn = () => {
            if (match_btn_info.value === "开始匹配") {
                console.log("send event: start-matching")
                // ws通信：socket.send表示向后端发送JSON数据，发起开始匹配请求
                store.state.pk.socket.send(JSON.stringify({
                    event: "start-matching",
                    bot_id: select_bot.value, // -1则为亲自上
                }));
                match_btn_info.value = "取消";

            } else {
                console.log("send event: stop-matching")
                // ws通信：socket.send表示向后端发送JSON数据，发起取消匹配请求
                store.state.pk.socket.send(JSON.stringify({
                    event: "stop-matching",
                }));
                match_btn_info.value = "开始匹配";

            }
        }
        // 从接口取出bots数据
        const refresh_bots = () => {
            $.ajax({
                url: "http://127.0.0.1:3001/user/bot/getlist/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    bots.value = resp;
                    console.log("refresh_resp:",resp);
                }
            })
        }
        refresh_bots();

        return {
            match_btn_info,
            click_match_btn,
            bots,
            select_bot
        }
    }
}
</script>

<style scoped>

div.matchground { 
    width: 60vw;
    height: 70vh;
    background-color: rgba(50, 50, 50, 0.5);
    margin: 40px auto;
}
div.user-photo {
    text-align: center;
}
div.user-photo > img {
    margin-top: 10vh;
    border-radius: 50%;
    width: 20vh;
    height:20vh;
}
div.user-username {
    text-align: center;
    font-size: 24px;
    font-weight: 600;
    color: white;
    padding-top: 2vh;
}
div.user-select-bot {
    padding-top:20vh;
}
div.user-select-bot > select {
    width: 60%;
    margin:0 auto;
}
</style>
