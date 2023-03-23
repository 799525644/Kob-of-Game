import { AcGameObject } from "./AcGameObject";
import { Wall } from "./Wall";
/**
 * GameMap.js文件，用于实现游戏地图（可自适应窗口、奇数）
 * 生成地图之后放在后端，用后端生成
 * 一、定义GameMap类（继承游戏基类）
 *      0.参数ctx：canvas对象，parent：DOM对象
 *      1.初始化[ctx、parent]，单位长度L，行r列c，墙数
 */

export class GameMap extends AcGameObject {
    constructor(ctx, parent) {
        super();

        this.ctx = ctx;
        this.parent = parent; // 获取GameMap组件的对应div信息
        this.L = 0;// 表示一个单位长度，用相对距离控制大小

        // 行列数一奇一偶，保证初始位分别为(奇数，奇数)；(偶数，奇数)，那么蛇头一定不可能同时走到一个位置变成平局
        this.rows = 13;
        this.cols = 14; 

        this.inner_walls_count = 20;
        this.walls = [];
    }

    // dfs算法判断连通性
    check_connectivity(g, sx, sy, tx, ty) {
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = true;


        let dx = [-1, 0, 1, 0], dy = [0, 1, 0, -1]; // 定义上下左右的偏移量
        for (let i = 0; i < 4; i ++) {
            let x = sx + dx[i], y = sy + dy[i];
            if (!g[x][y] && this.check_connectivity(g, x, y, tx, ty))
                return true;
        }

        return false;
    }
    create_walls() {
        // 0. 测试用
        // new Wall(0, 0, this); 
        // 1.四周、随机加上障碍物
        // 用数组先模拟障碍物，初始化障碍物数组
        const g = [];
        for (let r = 0; r < this.rows; r ++) {
            g[r] = [];
            for (let c = 0; c < this.cols; c ++) {
                g[r][c] = false;
            }
        }
        // 四周加上障碍物
        for (let r = 0; r < this.rows; r ++ ) {
            g[r][0] = g[r][this.cols - 1] = true;
        }

        for (let c = 0; c < this.cols; c ++)  {
            g[0][c] = g[this.rows - 1][c] = true;
        }

        // 随机创建轴对称障碍物；（避免重复暴力找空位来随机）
        for (let i = 0; i < this.inner_walls_count / 2; i++) {
            for (let j = 0; j < 1000; j++) {
                // random返回0-1，×数能得到0～数-1之间的值
                let r = parseInt(Math.random() * this.rows);
                let c = parseInt(Math.random() * this.cols);
                if (g[r][c] || g[c][r]) continue;
                // 避免左上角和右上角的出生位置
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2)
                    continue;
                // #1.轴对称，适用于正方形地图
                // g[r][c] = g[c][r] = true;
                // #2.中心对称，适用于长方形地图
                g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = true;

                break;
            }
        }

        // 如果不连通return false   
        const copy_g = JSON.parse(JSON.stringify(g));
        if (!this.check_connectivity(copy_g, this.rows - 2, 1, 1, this.cols - 2))
            return false;

        // 绘制障碍物
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if (g[r][c]) {
                    console.log(r + "-" + c)
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }
        return true;
    }
    
    // 开始循环生成障碍物，直到连通图的障碍物正确生成
    start() {
        for (let i = 0; i < 1000; i++)
            if (this.create_walls())
                break;

    }
    update_size() {
        // DOM.clientWidth、DOM.clientHeight获取div宽度和高度
        // parseInt，让canvas绘制完整的像素格，而不是以Math.min的浮点数绘制带白边的像素格
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
        // console.log(this.ctx.canvas.width, this.ctx.canvas.height);
    }
    update() {
        // console.log(this.L, this.ctx.canvas.width, this.ctx.canvas.height);
        this.update_size();
        this.render();
    }
    render() {
        // canvas使用：fillStyle是填充颜色，fillRect是控制大小
        // 0. 画一个纯绿色
        // this.ctx.fillStyle = 'green';
        // this.ctx.fillRect(0, 0, this.ctx.canvas.width, this.ctx.canvas.height)
        // 1. 画一个棋盘地图，偶数奇数颜色相间深浅绿：把颜色放入canvas块中
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