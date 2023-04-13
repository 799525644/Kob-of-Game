export default{
    state:{ // 状态属性定义，模块内通过context.state.xx获取
        status: "matching", // 和路由一样，一个表示匹配界面，一个表示对战界面
        socket: null,
        opponent_username: "",
        opponent_photo: "",
        gamemap: null,
    },
    getters:{
    },
    mutations:{ // 状态直接更新，通过context.commit()更新
        updateSocket(state, socket) {
            state.socket = socket;
        },
        updateOpponent(state, opponent) {
            state.opponent_username = opponent.username;
            state.opponent_photo = opponent.photo;
        },
        updateStatus(state, status) {
            state.status = status;
        },
        updateGamemap(state, gamemap) {
            state.gamemap = gamemap;
        }
    },
    actions:{ // 异步操作更新，通过dispatch调用
    },
    modules:{

    }
}