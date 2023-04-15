import { AcGameObject } from "./AcGameObject";
import { Wall } from "./Wall";
import { Snake } from "./Snake"

/**
 * GameMap.js文件，用于实现游戏地图（可自适应窗口、奇数）
 * 生成地图放在后端，用后端生成
 * 一、定义GameMap类（继承游戏基类）
 *      0.参数cavas对象，DOM对象（父），vuex store对象
 *      1.初始化[ctx、parent]，单位长度L，行r列c，墙数，墙数组，蛇数组
 */
export class GameMap extends AcGameObject {
    constructor(ctx, parent, store) {
        super();

        this.ctx = ctx;
        this.parent = parent; // 获取GameMap组件的对应div信息
        this.store = store;
        this.L = 0;// 表示一个单位长度，用相对距离控制大小

        // 行列数一奇一偶，保证初始位分别为(奇数，奇数)；(偶数，奇数)，那么蛇头一定不可能同时走到一个位置变成平局
        this.rows = 13;
        this.cols = 14; 

        this.inner_walls_count = 20;
        this.walls = [];

        this.snakes = [
            new Snake({id: 0, color:"#4876EC", r: this.rows - 2, c:1}, this),
            new Snake({id: 1, color:"#F94848", r: 1, c: this.cols - 2}, this),
        ];
    }

    create_walls() {
        console.log("create Wall!")
        const g = this.store.state.pk.gamemap;
        // 绘制障碍物
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if (g[r][c]) {
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }
    }
    
    // PART2:添加键盘移动监听事件(注意浏览器插件的快捷键冲突)
    add_listening_events(){
        console.log("listener~")
        this.ctx.canvas.focus();

        this.ctx.canvas.addEventListener("keydown",e => {
            let d = -1;
            if (e.key === 'w'|| e.key === `W`) d = 0;
            else if (e.key === 'd' || e.key ===`D`) d = 1;
            else if (e.key === 's'|| e.key ===`S`) d = 2;
            else if (e.key === 'a'|| e.key ===`A`) d = 3;
            // 如果是合法的移动操作，
            if (d >= 0) {
                // 把JSON变成字符串发过去，event名称，方向标识
                console.log("send key:"+d)
                this.store.state.pk.socket.send(JSON.stringify({
                    event: "move",
                    direction: d,
                }));
            }
        });
    }

    start() {
        this.create_walls();
        this.add_listening_events();
    }
    // PART1:更新地图大小
    update_size() {
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
        // console.log(this.ctx.canvas.width, this.ctx.canvas.height);
    }
    check_ready(){  // 检查两条蛇的状态（必须idle非死、指令已清空为-1）
        for (const snake of this.snakes){
            if(snake.status !== "idle") return false;
            if(snake.direction === -1 ) return false;
        }
        return true;
    }
    next_step() {   // 让两条蛇进入下一回合
        for (const snake of this.snakes) {
            snake.next_step();
        }
    }
    check_valid(cell){ // 检测目标位置是否合法（没有撞到身体和障碍物）
        for (const wall of this.walls) {
            if (wall.r === cell.r && wall.c === cell.c)
                return false;
        }
        for (const snake of this.snakes){
            let k = snake.cells.length;
            if(!snake.check_tail_increasing()){ // 当蛇尾前进时则可以到达，因此蛇尾处不用判断
                k -- ;
            }
            for (let i = 0; i < k; i ++){
                if(snake.cells[i].r === cell.r && snake.cells[i].c === cell.c)
                    return false;
            }
        }
        return true;
    }

    // PART1:更新（地图大小、渲染地图内容）
    update() {
        this.update_size();
        if(this.check_ready()){
            this.next_step();
        }
        this.render();
    }
    // PART1:渲染地图
    render() {
        // canvas使用：fillStyle是填充颜色，fillRect是控制大小
        // 0. 画一个纯绿色
        // this.ctx.fillStyle = 'green';
        // this.ctx.fillRect(0, 0, this.ctx.canvas.width, this.ctx.canvas.height)
        // 1. 画一个深浅绿色相间的棋盘地图：用canvas 2d颜色、位置与形状
        const color_even = "#AAD751", color_odd = "#A2D149";
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if ((r + c) % 2 == 0) {
                    this.ctx.fillStyle = color_even;
                } else {
                    this.ctx.fillStyle = color_odd;
                }
                // 画出当前坐标的canvas小块，前两个参数根据xy->列行即cr；后两个参数为边长
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
            }
        }
    }
}