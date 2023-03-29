<template>
    <ContentField>
        <div class="row justify-content-md-center">
            <div class="col-3">
                <form @submit.prevent="login">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="login-msg">{{ success_msg }}</div>
                    <button type="submit" class="btn btn-primary">提交</button>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue'
import {useStore} from 'vuex'
import {ref} from 'vue'
import router from '../../../router/index'


export default{
    components:{
        ContentField
    },
    setup(){
        const store = useStore();
        let username = ref('');
        let password = ref('');
        let success_msg = ref('');

        const login = () =>{
            success_msg.value = "";
            // 调用store中的login方法，{}中可以理解为传参
            store.dispatch("login",{
                username: username.value,
                password: password.value,
                success(){
                    store.dispatch("getinfo",{ // 组件调用store的getinfo函数
                        success(){
                            router.push({ name: 'home'})
                        },
                        error(){
                            success_msg.value = "用户信息读取失败" 
                        }
                    })
                },
                error(){
                    success_msg.value = "用户名或密码错误" 
                }
            })
        }
        return { // 暴露的属性或方法
            username,
            password,
            success_msg,
            login,
        }
    }
}
</script>


<style>
button{
    width: 100%;
}
div.login-msg{
    color:red;
}
</style>