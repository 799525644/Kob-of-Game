/**
 * Cell.js文件，用于实现蛇体
 * 一、定义Cell类（继承游戏基类）
 *      0.参数r，c表示行列位置
 *      1.初始化canvas块的位置r、c->转中央x,y位置
 */
export class Cell {
    constructor(r, c) {
        this.r = r;
        this.c = c;
        this.x = c + 0.5;
        this.y = r + 0.5;
    }
}
