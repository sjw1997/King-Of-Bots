<template>
    <div class="matchground">
        <div class="row" style="text-align: center;">
            <div class="col-12 user-tips">
                {{ user_tips }}
            </div>
            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.user.photo" alt="">
                </div>
                <div class="username">{{ $store.state.user.username }}</div>
            </div>
            <div class="col-4">
                <div class="select-bot">
                    <select :disabled="has_selected" v-model="bot_id" class="form-select" aria-label="Default select example">
                        <option value="-1" selected>亲自出马</option>
                        <option v-for="bot in bots" :key="bot.id" :value="bot.id">
                            {{ bot.title }}
                        </option>
                    </select>
                </div>
            </div>
            <div class="col-4">
                <div class="user-photo">
                    <img :src="$store.state.pk.opponent_photo" alt="">
                </div>
                <div class="username">{{ $store.state.pk.opponent_username }}</div>
            </div>
            <div class="col-12">
                <button @click="click_btn" type="button" class="btn btn-warning btn-lg">
                    {{ btn_message }}
                </button>
            </div>
        </div>
    </div>
</template>

<script>
import store from '@/store';
import { ref } from 'vue';
import $ from 'jquery';

export default {
    setup() {
        let btn_message = ref("开始匹配");
        let user_tips = ref("");
        let bots = ref([]);
        let bot_id = ref(-1);
        let has_selected = ref(false);

        const click_btn = () => {
            let data = null;
            if (btn_message.value === "开始匹配") {
                has_selected.value = true;
                btn_message.value = "取消";
                user_tips.value = "寻找对手中...";
                data = {
                    event: "start-matching",
                    bot_id: bot_id.value,
                };
            } else {
                has_selected.value = false;
                btn_message.value = "开始匹配";
                user_tips.value = "";
                data = {
                    event: "stop-matching"
                };
            }
            store.state.pk.socket.send(JSON.stringify(data));
        };


        const refresh_bots = () => {
            $.ajax({
                url: "http://192.168.0.110:3000/user/bot/getlist/",
                type: "GET",
                headers: {
                    Authorization: `Bearer ${store.state.user.token}`
                },
                success(resp) {
                    bots.value = resp;
                },
                error(resp) {
                    console.log(resp);
                }
            })
        };

        refresh_bots();

        return {
            click_btn,
            btn_message,
            user_tips,
            bots,
            bot_id,
            has_selected,
        }
    }
}

</script>

<style scoped>
.matchground {
    width: 60vw;
    height: 70vh;
    margin: 40px auto;
    background-color: rgba(50, 50, 50, 0.5);
}

.user-tips {
    position: absolute;
    top: 20vh;
    left: 0vw;
    font-size: 30px;
}

div > img {
    width: 10vh;
    margin-top: 20vh;
    border-radius: 50%;
}

.username {
    font-size: 24px;
    font-weight: bold;
    margin-top: 10px;
    color: white;
}

.select-bot {
    margin-top: 25vh;
}
</style>