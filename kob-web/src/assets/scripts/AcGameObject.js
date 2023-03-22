/**
 * AcGameObject.js文件，用于实现游戏基类功能
 * 一、定义AcGameObject类：游戏基类
 *      1.初始化时加入列表、帧时间间隔、开始标记
 *      2.自定义生命周期方法start()、update()、on_destroy()、destroyed()
 * 二、刷新器requestAnimationFrame
 */
// 游戏Obj列表
const AC_GAME_OBJECTS = [];

// 定义AcGameObject类（不是export default，外部引入需要加{}）
export class AcGameObject{
    constructor(){
        AC_GAME_OBJECTS.push(this);
        this.timedelta = 0; // （帧）时间间隔
        this.has_called_start = false; // 用于记录是否执行过
    }
    start(){ // 只执行一次

    }

    update(){ // 每次渲染执行一次，除了第一次

    }

    on_destroy(){ // 删除之前执行

    }

    destroyed() {
        this.on_destroy();
        

        for(let i in AC_GAME_OBJECTS){
            const obj = AC_GAME_OBJECTS[i];
            if( obj === this){
                AC_GAME_OBJECTS.splice(i);
                break;
            }
        }
    }
}

let last_timestamp;// 上一次执行的时间

const step = (timestamp) => {
    for(let obj of AC_GAME_OBJECTS){
        if(!obj.has_called_start){
            obj.has_called_start = true;
            obj.start();
        }else{
            obj.timedelta = timestamp - last_timestamp;
            obj.update();
        }
    }

    last_timestamp = timestamp;
    requestAnimationFrame(step)
}
// 刷新器：请求下一帧
requestAnimationFrame(step)

