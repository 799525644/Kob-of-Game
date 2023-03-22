import { AcGameObject } from "./AcGameObject";

/**
 * Wall.js文件，用于实现游戏地图中的墙壁
 * 一、定义Wall类（继承游戏基类）
 *      0.参数行r、列c、gamemap对象
 *      1.初始化[r、c、gamemap]、墙颜色
 * Tips：类中的this表示当前对象实例
 */

export class Wall extends AcGameObject{
    constructor(r, c, gamemap){
        super();
        this.r = r; 
        this.c = c;
        this.gamemap = gamemap;
        this.color = "#B37226" // 墙的颜色
    }

    update(){
        this.render();
    }

    render() {
            const L = this.gamemap.L;// 单位格需要动态取，因为L会动态变化
            const ctx = this.gamemap.ctx;

            ctx.fillStyle = this.color;
            ctx.fillRect(this.c * L, this.r * L, L, L);
    }

}