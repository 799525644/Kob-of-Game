import $ from "jquery";
export default{
    state:{ // 状态属性定义，模块内通过context.state.xx获取
        id:"",
        username:"",
        photo:"",
        token:"",
        is_login: false,
    },
    getters:{
    },
    mutations:{ // 状态更新，模块内通过context.commit()更新
        updateUser(state, user){
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.is_login = user.is_login;
        },
        updateToken(state, token){
            state.token = token;
        },
        logout(state){ // logout，本质上就是清空token
            state.id = "";
            state.username = "";
            state.photo = "";
            state.token = "";
            state.is_login = false;
        }
    },
    actions:{ // 组件调用，由组件中store.dispatch()调用
        login(context, data){// context为上下文，data为组件调用参数
            $.ajax({
                url:"http://127.0.0.1:3000/user/account/token/",
                type:"post",// type处大小写任意
                data:{
                  username: data.username,
                  password: data.password,
                },
                success(resp){ // resp为后端返回的结果
                    if(resp.success_msg === "success"){
                        console.log("符合")
                        context.commit("updateToken", resp.token);
                        data.success(resp)
                    }
                    else {
                        data.error(resp);
                    }
                },
                error(resp){
                    data.error(resp)
                }
              });
        },
        getinfo(context, data){// context为上下文，data为组件调用参数
            // 获取用户相关信息，一般用get。url、type➕header带授权token
            $.ajax({
                url:"http://127.0.0.1:3000/user/account/info/",
                type:"get",
                headers:{
                    Authorization: "Bearer " + context.state.token, // 授权
                },
                success(resp){
                    if(resp.success_msg === "success"){
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
                    console.log("resp_error",resp)
                    data.error(resp)
                }
            })
        },
        logout(context){
            context.commit("logout");
        }
    },
    modules:{

    }
}