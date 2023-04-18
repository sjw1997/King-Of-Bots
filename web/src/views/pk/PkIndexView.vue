<template>
    <PlayGround v-if="$store.state.pk.status === 'playing'"/>
    <MatchGround v-if="$store.state.pk.status === 'matching'" />
</template>

<script>
import PlayGround from '@/components/PlayGround.vue'
import MatchGround from '@/components/MatchGround.vue'
import { onMounted, onUnmounted } from 'vue';
import { useStore } from 'vuex';

export default {
    components: {
        PlayGround,
        MatchGround,
    },
    setup() {
        const store = useStore();
        let socket = null;

        onMounted(() => {
            store.commit("updateOpponent", {
                username: "我的对手",
                photo: "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png"
            });
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
                console.log(data);
                if (data.event === "start-matching") {
                    store.commit("updateOpponent", {
                        username: data.opponent_username,
                        photo: data.opponent_photo,
                    });
                    store.commit("updateGamemap", data.gamemap);
                    setTimeout(() => {
                        store.commit("updateStatus", "playing");
                    }, 1500);
                }
            };
            socket.onclose = () => {
                console.log("disconnected");
            }
        });  

        onUnmounted(() => {
            socket.close();
            store.commit("updateStatus", "matching");
        });
    }
}
</script>

<style scoped>

</style>