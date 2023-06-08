import $ from "jquery";
export default{
    state:{ // 状态属性定义，模块内通过context.state.xx获取
        id:"",
        username:"",
        photo:"",
        token:"",
        is_login: false,
        pulling_info: true, // 判断是否在拉取用户信息
    },
    getters:{
    },
    mutations:{ // 状态直接更新，通过context.commit()更新
        // updateUser，对User信息赋值更新
        updateUser(state, user){ 
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.is_login = user.is_login;
        },
        // updateToken：对token赋值更新
        updateToken(state, token){ 
            state.token = token;
        },
        // updatePullingInfo：标识加载状态
        updatePullingInfo(state, pulling_info){
            state.pulling_info = pulling_info;
        },
        // logout：对User信息和token清空删除
        logout(state){ 
            state.id = "";
            state.username = "";
            state.photo = "";
            state.token = "";
            state.is_login = false;
        },
    },
    actions:{ // 异步操作更新，通过dispatch调用
        //login：获得token并持久化，接口/user/account/token/
        login(context, data){
            // context为上下文，data为组件调用参数
            $.ajax({
                url: "https://app5581.acapp.acwing.com.cn/api/user/account/token/",
                // url:"http://127.0.0.1:3000/user/account/token/",
                type:"post",// type处大小写任意
                data:{
                  username: data.username,
                  password: data.password,
                },
                success(resp){ // resp为后端返回的结果
                    console.log("user_login_resp:",resp);
                    context.commit("updateToken", resp.token);
                    localStorage.setItem("jwt_token",resp.token);
                    if(resp.error_message === "success"){
                        data.success(resp)
                    }
                    else {
                        data.error(resp);
                    }
                },
                error(resp){
                    console.log("user_login_resp:",resp);
                    data.error(resp)
                }
              });
        },
        // getinfo：获取当前用户的信息，header带token，接口/user/account/info/
        getinfo(context, data){
            $.ajax({
                url: "https://app5581.acapp.acwing.com.cn/api/user/account/info/",
                // url:"http://127.0.0.1:3000/user/account/info/",
                type:"get",
                headers:{
                    Authorization: "Bearer " + context.state.token, // 授权
                },
                success(resp){ //当成功时执行
                    if(resp.error_message === "success"){
                        context.commit("updateUser",{
                            ...resp, // 将resp解构
                            is_login: true,
                        })
                        data.success(resp);
                    }else{
                        data.error(resp)
                    }
                },
                error(resp){
                    console.log("error_message",resp)
                    data.error(resp)
                }
            })
        },
        // logout：登出则清空token
        logout(context){
            localStorage.removeItem("jwt_token");
            context.commit("logout");
        }
    },
    modules:{

    }
}