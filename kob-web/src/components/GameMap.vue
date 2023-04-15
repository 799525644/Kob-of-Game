<!-- 
  PK模块->背景PlayGround->**游戏面板GameMap|匹配面板MatchGround
-->
<template>
    <!-- vue的变量绑定使用ref -->
    <div  ref="parent" class="gamemap" >
        <canvas ref="canvas" tabindex="0"></canvas>
    </div>
</template>

<script>
import { GameMap } from '@/assets/scripts/GameMap';
import { ref, onMounted } from 'vue' // 定义ref变量，onMounted用于vue生命周期的挂载时期
import { useStore } from 'vuex';
export default {
    // setup的一般函数写法可以用this，箭头函数写法没有this，回向上层指
    setup() {
        let parent = ref(null);
        let canvas = ref(null);
        const store = useStore();

        onMounted(()=>{ // 组件挂载后，创建GameMap对象；取vue中的变量需要.value
            store.commit(
                "updateGameObject",
                new GameMap(
                    canvas.value.getContext('2d'),
                    parent.value, 
                    store)
                );
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