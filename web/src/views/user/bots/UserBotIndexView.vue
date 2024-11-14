<template>
    <div class="container">
        <div class="row">
            <div class="col-md-3 col-sm-12">
                <div class="card">
                    <div class="card-body" style="margin: 0 auto;">
                        <img :src="$store.state.user.photo" class="img-fluid" alt="">
                    </div>
                </div>
            </div>
            <div class="col-md-9 col-sm-12">
                <div class="card">
                    <div class="card-header">
                        <span style="font-size: 130%;">我的Bot</span>
                        <button type="button" 
                        class="btn btn-primary float-end"
                        data-bs-toggle="modal" data-bs-target="#add-bot-btn">
                            创建Bot
                        </button>
                        <EditModal 
                        modal_id="add-bot-btn"
                        modal_title="创建Bot" 
                        @refresh_bots="refresh_bots"/>
                    </div>
                    <div class="card-body">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>名称</th>
                                    <th>创建时间</th>
                                    <th>语言</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="bot in bots" :key="bot.id">
                                    <td>{{ bot.title }}</td>
                                    <td>{{ bot.createtime }}</td>
                                    <td>{{ bot.language }}</td>
                                    <td>
                                        <button type="button" class="btn btn-secondary btn-sm" 
                                        style="margin-right: 5px;"
                                        data-bs-toggle="modal" 
                                        :data-bs-target="'#update-bot-btn-' + bot.id">
                                            修改
                                        </button>
                                        <EditModal 
                                        :modal_id="'update-bot-btn-' + bot.id" 
                                        modal_title="修改Bot"
                                        :modal_bot="bot"
                                        @refresh_bots="refresh_bots"/>
                                        <button type="button" class="btn btn-danger btn-sm" @click="remove_bot(bot.id)">删除</button>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import $ from 'jquery';
import { useStore } from 'vuex';
import { ref } from 'vue';
import EditModal from '@/components/EditModal.vue';

export default {
    components: {
        EditModal,
    },
    setup() {
        const store = useStore();
        let bots = ref([]);

        const refresh_bots = () => {
            $.ajax({
                url: "https://app5212.acapp.acwing.com.cn/api/user/bot/getlist/",
                type: "GET",
                headers: {
                    Authorization: `Bearer ${store.state.user.token}`
                },
                success(resp) {
                    bots.value = resp;
                }
            })
        };

        refresh_bots();

        const remove_bot = (bot_id) => {
            $.ajax({
                url: "https://app5212.acapp.acwing.com.cn/api/user/bot/remove/",
                type: "POST",
                data: {
                    bot_id
                },
                headers: {
                    Authorization: `Bearer ${store.state.user.token}`
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        refresh_bots();
                    }
                }
            });
        };

        return {
            bots,
            remove_bot,
            refresh_bots,
        }
    }
}
</script>

<style scoped>
.card {
    margin-top: 20px;
}
</style>