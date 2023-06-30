<template>
    <div class="snake-explanation">
        <div>
            <span class="snake-explanation-color" style="color: #4876EC;">蓝方: </span>
            <span>{{ a_username }}</span>
        </div>
        <div>
            <span class="snake-explanation-color" style="color: #F94848;">红方: </span>
            <span>{{ b_username }}</span>
        </div>
    </div>
    <div class="playground">
        <GameMap />
    </div>
</template>

<script>
import GameMap from '@/components/GameMap.vue'
import { useStore } from 'vuex';
import { computed } from 'vue';

export default {
    components: {
        GameMap,
    },
    setup() {
        const store = useStore();

        let a_username = computed(() => {
            if (store.state.record.is_record) {
                return store.state.record.a_username;
            } else {
                if (store.state.pk.a_id === parseInt(store.state.user.id)) {
                    return store.state.user.username;
                }
                return store.state.pk.opponent_username;
            }
        });
        let b_username = computed(() => {
            if (store.state.record.is_record) {
                return store.state.record.b_username;
            } else {
                if (store.state.pk.b_id === parseInt(store.state.user.id)) {
                    return store.state.user.username;
                }
                return store.state.pk.opponent_username;
            }
        });

        return {
            a_username,
            b_username,
        }
    }
}

</script>

<style scoped>
.playground {
    width: 60vw;
    height: 70vh;
    margin: 20px auto;
}

.snake-explanation {
    width: 100%;
    height: 30px;
    margin-top: 2vh;
    margin-bottom: 2vh;
    text-align: center;
}

.snake-explanation-color {
    font-size: 18px;
    font-weight: bold;
}

</style>