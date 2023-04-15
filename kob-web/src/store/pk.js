export default{
    state:{ // 状态属性定义，模块内通过context.state.xx获取
        status: "matching", // 和路由一样，一个表示匹配界面，一个表示对战界面
        socket: null,
        opponent_username: "",
        opponent_photo: "",
        gamemap: null,
        a_id: 0,
        a_sx: 0,
        a_sy: 0,
        b_id: 0,
        b_sx: 0,
        b_sy: 0,
        gameObject: null,
        loser: "none",  // none、all、A、B

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
        updateGame(state, game) {
            state.gamemap = game.map;
            state.a_id = game.a_id;
            state.a_sx = game.a_sx;
            state.a_sy = game.a_sy;

            state.b_id = game.b_id;
            state.a_sx = game.b_sx;
            state.a_sy = game.b_sy;
        },
        updateGameObject(state, gameObject){
            state.gameObject = gameObject;
        },
        updateLoser(state, loser){
            state.loser = loser;
        }
    },
    actions:{ // 异步操作更新，通过dispatch调用
    },
    modules:{

    }
}