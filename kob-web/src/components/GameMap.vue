<!-- 
    PK模块
        背景PlayGround
            ->>游戏面板GameMap
-->
<template>
    <!-- vue的变量绑定使用ref -->
    <div  ref="parent" class="gamemap" >
        <canvas ref="canvas"></canvas>
    </div>
</template>

<script>
import { GameMap } from '@/assets/scripts/GameMap';
import { ref, onMounted } from 'vue' // ref用于vue的脚本中定义变量，onMounted用于vue生命周期的挂载时期
export default {
    // setup的一般函数写法可以用this，箭头函数写法没有this，回向上层指
    setup() {
        let parent = ref(null);
        let canvas = ref(null);

        onMounted(()=>{ // 组件挂载后，创建GameMap对象；取vue中的变量需要.value
            // vue的传值
            // canvas.value.getContext('2d')即传入了canvas.getContext('2d')，一个 2D 画布的渲染上下文
            // parent.value即传入parent也就是gamemap的div信息
            new GameMap(canvas.value.getContext('2d'), parent.value)
        });
        return{ // 返回后才能在 template中使用变量
            parent,
            canvas
        }
    }
}
</script>

<style scoped>
/* 注意样式表内的标点符号 */
div.gamemap{
    width: 100%;
    height: 100%;
    /* 1. 实现居中 */
    display: flex;
    /* 1.1 竖直居中 */
    justify-content: center;
    /* 1.2 水平居中 */
    align-items: center; 
}
</style>