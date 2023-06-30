<template>
    <div ref="round" class="round">第 1 回合</div>
    <div ref="timer" class="timer" v-if="!$store.state.record.is_record">
        倒计时： 5
    </div>
    <div ref="parent" class="gamemap">
        <canvas ref="canvas" tabindex="0"></canvas>
    </div>
</template>

<script>
import { GameMap } from '@/assets/scripts/GameMap';
import { ref, onMounted } from 'vue';
import { useStore } from 'vuex';

export default {
    setup() {
        let parent = ref(null);
        let canvas = ref(null);
        let timer = ref(null);
        let round = ref(null);
        
        const store = useStore();

        onMounted(() => {
            const gameObject = new GameMap(canvas.value.getContext('2d'), parent.value, timer.value, round.value, store);
            store.commit("updateGameObject", gameObject);
        });

        return {
            parent,
            canvas,
            timer,
            round,
        }
    }
}

</script>

<style scope>
.gamemap {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}

.timer {
    width: 100%;
    height: 30px;
    margin-bottom: 2vh;
    text-align: center;
    font-size: 28px;
    font-weight: bold;
}

.round {
    width: 100%;
    height: 30px;
    margin-top: 5vh;
    margin-bottom: 2vh;
    text-align: center;
    font-size: 30px;
    font-weight: bold;
    font-style: italic;
}
</style>