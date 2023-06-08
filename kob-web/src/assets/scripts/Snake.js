import { AcGameObject } from "./AcGameObject";
import { Cell } from "./Cell";

export class Snake extends AcGameObject {
    constructor(info, gamemap) {
        super();

        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap;

        this.cells = [new Cell(info.r, info.c)];  // 存放蛇的身体，cells[0]存放蛇头
        this.next_cell = null;  // 下一步的目标位置

        this.speed = 5;  // 蛇每秒走5个格子
        this.direction = -1;  // -1表示没有指令，0、1、2、3表示上右下左
        this.status = "idle";  // idle表示静止，move表示正在移动，die表示死亡

        this.dr = [-1, 0, 1, 0];  // 4个方向行的偏移量
        this.dc = [0, 1, 0, -1];  // 4个方向列的偏移量

        this.step = 0;  // 表示回合数
        this.eps = 1e-2;  // 允许的误差

        // 蛇的眼睛（画图很清楚，蛇头中点为基准）
        this.eye_direction = 0;
        if (this.id === 1) this.eye_direction = 2;  // 左下角的蛇初始朝上，右上角的蛇朝下
        this.eye_dx = [  // 蛇眼睛不同方向的x的偏移量（相对偏移），1-3，2-4一致
            [-1, 1],
            [1, 1],
            [1, -1],
            [-1, -1],
        ];
        this.eye_dy = [  // 蛇眼睛不同方向的y的偏移量
            [-1, -1],
            [-1, 1],
            [1, 1],
            [1, -1],
        ]
    }

    start() {

    }

    set_direction(d) {
        this.direction = d;
    }

    // 检测蛇尾是否增加（前10回合。后10回合每3回合增长）
    check_tail_increasing() {  // 检测当前回合，蛇的长度是否增加
        if (this.step <= 10) return true;
        if (this.step % 3 === 1) return true;
        return false;
    }

    next_step() {  // 将蛇的状态变为走下一步
        const d = this.direction;
        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);
        this.eye_direction = d;
        this.direction = -1;  // 清空操作
        this.status = "move";
        this.step ++ ;

        const k = this.cells.length;
        for (let i = k; i > 0; i -- ) {
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1]));
        }

        // 蛇是否死亡让后端判断
        // if (!this.gamemap.check_valid(this.next_cell)) {  // 下一步操作撞了，蛇瞬间去世
        //     this.status = "die";
        // }
    }

    update_move() {
        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        const distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < this.eps) {  // 走到目标点了
            this.cells[0] = this.next_cell;  // 添加一个新蛇头
            this.next_cell = null;
            this.status = "idle";  // 走完了，停下来

            // 蛇不变长
            if (!this.check_tail_increasing()) {  
                this.cells.pop();
            }
        } else {
            const move_distance = this.speed * this.timedelta / 1000;  // 每两帧之间走的距离
            this.cells[0].x += move_distance * dx / distance;
            this.cells[0].y += move_distance * dy / distance;
   
            // 如果蛇不变长，则去尾增头
            if (!this.check_tail_increasing()) {
                const k = this.cells.length;
                const tail = this.cells[k - 1], tail_target = this.cells[k - 2];
                const tail_dx = tail_target.x - tail.x;
                const tail_dy = tail_target.y - tail.y;
                tail.x += move_distance * tail_dx / distance;
                tail.y += move_distance * tail_dy / distance;
            }
        }
    }

    update() {  // 每一帧执行一次
        if (this.status === 'move') {
            this.update_move();
        }

        this.render();
    }

    render() {
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;
        ctx.fillStyle = this.color;
        // 死亡形态：变白
        if (this.status === "die") {
            ctx.fillStyle = "white";
        }
        // 画蛇身体：先圆形
        for (const cell of this.cells) {
            ctx.beginPath();
            ctx.arc(cell.x * L, cell.y * L, L / 2 * 0.8, 0, Math.PI * 2);
            ctx.fill();
        }
        // 画蛇身体圆润化：相邻部分填充矩阵
        for (let i = 1; i < this.cells.length; i ++ ) {
            const a = this.cells[i - 1], b = this.cells[i];
            // 边界情况：重合
            if (Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps)
                continue;
            if (Math.abs(a.x - b.x) < this.eps) {
                // 横相邻，考虑到身体不要太胖，宽度0.8L，矩阵起点为：x-0.5+0.1的偏移量
                ctx.fillRect((a.x - 0.4) * L, Math.min(a.y, b.y) * L, L * 0.8, Math.abs(a.y - b.y) * L);
            } else {
                // 纵相邻，同理
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.4) * L, Math.abs(a.x - b.x) * L, L * 0.8);
            }
        }

        // 画蛇的眼睛
        ctx.fillStyle = "black";
        for (let i = 0; i < 2; i ++ ) {
            // 蛇头单位体的中点位置+上下左右1基准偏移量*0.15*map的单位L
            const eye_x = (this.cells[0].x + this.eye_dx[this.eye_direction][i] * 0.15) * L;
            const eye_y = (this.cells[0].y + this.eye_dy[this.eye_direction][i] * 0.15) * L;

            ctx.beginPath();
            ctx.arc(eye_x, eye_y, L * 0.05, 0, Math.PI * 2);
            ctx.fill();
        }
    }
}
