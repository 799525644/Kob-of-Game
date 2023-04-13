import { AcGameObject } from "./AcGameObject";
import { Wall } from "./Wall";
import { Snake } from "./Snake"

/**
 * GameMap.js文件，用于实现游戏地图（可自适应窗口、奇数）
 * 生成地图之后放在后端，用后端生成
 * 一、定义GameMap类（继承游戏基类）
 *      0.参数ctx：canvas对象，parent：DOM对象
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

    // #1 PART1:dfs算法判断连通性（转移到后端）
    // check_connectivity(g, sx, sy, tx, ty) {
    //     if (sx == tx && sy == ty) return true;
    //     g[sx][sy] = true;

    //     let dx = [-1, 0, 1, 0], dy = [0, 1, 0, -1]; // 定义上下左右的偏移量
    //     for (let i = 0; i < 4; i ++) {
    //         let x = sx + dx[i], y = sy + dy[i];
    //         if (!g[x][y] && this.check_connectivity(g, x, y, tx, ty))
    //             return true;
    //     }

    //     return false;
    // }
    //PART1:创建障碍物
    create_walls() {
        const g = this.store.state.pk.gamemap;
        // // #1 前端生成地图
        // // 0. 测试用
        // new Wall(0, 0, this); 
        // // 1.四周、随机加上障碍物
        // // 用数组先模拟障碍物，初始化障碍物数组
        // const g = [];
        // for (let r = 0; r < this.rows; r ++) {
        //     g[r] = [];
        //     for (let c = 0; c < this.cols; c ++) {
        //         g[r][c] = false;
        //     }
        // }
        // // 四周加上障碍物
        // for (let r = 0; r < this.rows; r ++ ) {
        //     g[r][0] = g[r][this.cols - 1] = true;
        // }

        // for (let c = 0; c < this.cols; c ++)  {
        //     g[0][c] = g[this.rows - 1][c] = true;
        // }

        // 随机创建轴对称障碍物；（避免重复暴力找空位来随机）
        // for (let i = 0; i < this.inner_walls_count / 2; i++) {
        //     for (let j = 0; j < 1000; j++) {
        //         // random返回0-1，×数能得到0～数-1之间的值
        //         let r = parseInt(Math.random() * this.rows);
        //         let c = parseInt(Math.random() * this.cols);
        //         // 如果已经设墙，则跳过
        //         if (g[r][c] || g[this.rows - 1 - r][this.cols - 1 - c]) continue;
        //         // 避免左上角和右上角的出生位置
        //         if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2)
        //             continue;
        //         // #1.轴对称，适用于正方形地图
        //         // g[r][c] = g[c][r] = true;
        //         // #2.中心对称，适用于长方形地图
        //         g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = true;
        //         break;
        //     }
        // }

        // 如果不连通return false   
        // const copy_g = JSON.parse(JSON.stringify(g));
        // if (!this.check_connectivity(copy_g, this.rows - 2, 1, 1, this.cols - 2))
        //     return false;

        // 绘制障碍物
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if (g[r][c]) {
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }
        // return true;
    }
    
    // PART2:添加键盘移动监听事件(注意浏览器插件的快捷键冲突)
    add_listening_events(){
        this.ctx.canvas.focus();

        const [snake0, snake1] = this.snakes;
        this.ctx.canvas.addEventListener("keydown",e => {
            console.log("监听键盘",e.key)
            if (e.key  === 'w') snake0.set_direction(0);
            else if (e.key === 'd') snake0.set_direction(1);
            else if (e.key === 's') snake0.set_direction(2);
            else if (e.key === 'a') snake0.set_direction(3);
            else if (e.key === 'ArrowUp') snake1.set_direction(0);
            else if (e.key === 'ArrowRight') snake1.set_direction(1);
            else if (e.key === 'ArrowDown') snake1.set_direction(2);
            else if (e.key === 'ArrowLeft') snake1.set_direction(3);
        });
    }

    // PART1:循环执行障碍物生成，直到连通判定成功; PART2:执行键盘监听
    start() {
        // #1 循环1000次
        // for (let i = 0; i < 1000; i++)
        //     if (this.create_walls())
        //         break;
        this.create_walls();
        this.add_listening_events();
    }
    // PART1:更新地图大小
    update_size() {
        // DOM.clientWidth、DOM.clientHeight获取div宽度和高度
        // parseInt，让canvas绘制完整的像素格，而不是以Math.min的浮点数绘制带白边的像素格
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
        // console.log(this.ctx.canvas.width, this.ctx.canvas.height);
    }
    // PART2:两条蛇的移动准备
    check_ready(){  // 检查两条蛇的状态（必须idle非死、指令已清空为-1）
        for (const snake of this.snakes){
            if(snake.status !== "idle") return false;
            if(snake.direction === -1 ) return false;
        }
        return true;
    }
    next_step() {  // 让两条蛇进入下一回合
        for (const snake of this.snakes) {
            snake.next_step();
        }
    }
    check_valid(cell){ // 检测目标位置是否合法
        // 不能撞到障碍物
        for (const wall of this.walls) {
            if (wall.r === cell.r && wall.c === cell.c)
                return false;
        }
        // 不能撞到身体，除去蛇尾
        for (const snake of this.snakes){
            let k = snake.cells.length;
            if(!snake.check_tail_increasing()){ // 当蛇尾前进时则可以到达，蛇尾处不用判断
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
        // console.log(this.L, this.ctx.canvas.width, this.ctx.canvas.height);
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