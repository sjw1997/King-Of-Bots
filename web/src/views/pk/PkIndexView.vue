<template>
    <PlayGround v-if="$store.state.pk.status === 'playing'"/>
    <MatchGround v-if="$store.state.pk.status === 'matching'" />
    <ResultBoard v-if="$store.state.pk.loser !== 'none'" />
</template>

<script>
import PlayGround from '@/components/PlayGround.vue'
import MatchGround from '@/components/MatchGround.vue'
import ResultBoard from '@/components/ResultBoard.vue'
import { onMounted, onUnmounted } from 'vue';
import { useStore } from 'vuex';

export default {
    components: {
        PlayGround,
        MatchGround,
        ResultBoard,
    },
    setup() {
        const store = useStore();
        let socket = null;

        onMounted(() => {
            store.commit("updateOpponent", {
                username: "我的对手",
                photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png"
            });
            store.commit("updateIsRecord", false);
            const url = `ws://192.168.0.110:3000/websocket/${store.state.user.token}/`;
            socket = new WebSocket(url);
            store.commit("updateSocket", socket);

            socket.onopen = () => {
                console.log("connected");
            };
            socket.onclose = () => {
                console.log("disconnected");
            };
            socket.onmessage = (msg) => {
                const data = JSON.parse(msg.data);
                if (data.event === "start-matching") {
                    store.commit("updateOpponent", {
                        username: data.opponent_username,
                        photo: data.opponent_photo,
                    });
                    store.commit("updateGame", data.game);
                    store.commit("updateStatus", "playing");
                    setTimeout(() => {
                        store.commit("updateStatus", "playing");
                    }, 200);
                } else if (data.event === "move") {
                    const gameObject = store.state.pk.gameObject;
                    const [snakeA, snakeB] = gameObject.snakes;
                    snakeA.set_direction(data.a_direction);
                    snakeB.set_direction(data.b_direction);
                } else if (data.event === "result") {
                    const gameObject = store.state.pk.gameObject;
                    const [snakeA, snakeB] = gameObject.snakes;
                    if (data.loser === "all") {
                        snakeA.status = snakeB.status = "die";
                    } else if (data.loser === "A") {
                        snakeA.status = "die";
                    } else if (data.loser === "B") {
                        snakeB.status = "die";
                    }
                    store.commit("updateLoser", data.loser);
                }
            };
            socket.onclose = () => {
                console.log("disconnected");
            }
        });  

        onUnmounted(() => {
            socket.close();
            store.commit("updateStatus", "matching");
            store.commit("updateLoser", "none");
        });
    }
}
</script>

<style scoped>

</style>