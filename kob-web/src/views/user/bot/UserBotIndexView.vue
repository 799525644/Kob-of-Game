<template>
    <ContentField>我的机器人</ContentField>
</template>

<script>
import { useStore } from 'vuex';
import ContentField from '../../../components/ContentField.vue'
import $ from 'jquery'
export default{
    components:{
        ContentField
    },
    setup(){
        const store = useStore();
        // 添加接口测试
        // $.ajax({
        //     url:"http://127.0.0.1:3001/user/bot/add/",
        //     type:"POST",
        //     data:{
        //         title:"Bot的标题",
        //         description:"Bot的描述",
        //         content:"Bot的代码",
        //     },
        //     headers:{
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp){
        //         console.log(resp)
        //     },
        //     error(resp){
        //         console.log(resp)
        //     } 
        // })

        // 删除接口测试
        // $.ajax({
        //     url:"http://127.0.0.1:3001/user/bot/remove/",
        //     type:"POST",
        //     data:{
        //         bot_id: 4,
        //     },
        //     headers:{
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp){
        //         console.log(resp)
        //     },
        //     error(resp){
        //         console.log(resp)
        //     } 
        // })

        // 更新接口测试
        // $.ajax({
        //     url:"http://127.0.0.1:3001/user/bot/update/",
        //     type:"POST",
        //     data:{
        //         bot_id: 7,
        //         title: "Bot的标题",
        //         description:"Bot的描述",
        //         content: "Bot的代码",
        //     },
        //     headers:{
        //         Authorization: "Bearer " + store.state.user.token,
        //     },
        //     success(resp){
        //         console.log(resp)
        //     },
        //     error(resp){
        //         console.log(resp)
        //     } 
        // })

        // 获取接口测试
        $.ajax({
            url: "http://127.0.0.1:3001/user/bot/getlist/",
            type: "get",
            headers: {
                Authorization: "Bearer " + store.state.user.token,
            },
            success(resp) {
                console.log(resp);
            },
            error(resp) {
                console.log(resp);
            }
        })

    }
}
</script>

<style>
</style>