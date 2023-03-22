<!-- 
    PK模块：PkIndexView
        ->>PK幕布：PlayGround
            游戏地图：GameMap
-->
<template>
    <div class = "playground">
        <GameMap />
    </div>
</template>

<script>
// 用到的组件需要显性表示
import GameMap from './GameMap.vue';
export default{
    components:{
        GameMap,
    }
}
</script>

<style scoped>
    div.playground{
        width: 60vw;
        height: 70vh;
        background:lightblue;
        /* 上下40 左右居中 */
        margin: 40px auto; 
    }
</style>



