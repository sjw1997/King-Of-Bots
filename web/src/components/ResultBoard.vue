<template>
    <div class="result-board">
        <div class="result-board-text">
            {{ whichLoser }}
        </div>
        <div class="result-board-button">
            <button @click="restart" type="button" class="btn btn-warning btn-lg">
                再来！
            </button>
        </div>
    </div>
</template>

<script>
import { useStore } from 'vuex';

export default {
    computed: {
        whichLoser() {
            const store = useStore();
            const loser = store.state.pk.loser;
            let res = "";
            if (loser === "all") {
                res = "平局";
            } else if (loser === "A" && store.state.pk.a_id === parseInt(store.state.user.id)) {
                res = "失败";
            } else if (loser === "B" && store.state.pk.b_id === parseInt(store.state.user.id)) {
                res = "失败";
            } else {
                res = "胜利";
            }
            return res;
        }
    },
    setup() {
        const store = useStore();
        const restart = () => {
            store.commit("updateStatus", "matching");
            store.commit("updateLoser", "none");
            store.commit("updateOpponent", {
                username: "我的对手",
                photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png"
            });
        }

        return {
            restart
        }
    }
}
</script>

<style scoped>
.result-board {
    width: 20vw;
    height: 30vh;
    background-color: rgba(50, 50, 50, 0.5);
    position: absolute;
    top: 30vh;
    left: 40vw;
    text-align: center;
    box-sizing: border-box;
}

.result-board-text {
    padding-top: 10vh;
    font-size: 50px;
    color: white;
    font-style: italic;
}

.result-board-button {
    padding-top: 7vh;
}
</style>