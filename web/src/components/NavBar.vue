<template>
    <nav class="navbar sticky-top navbar-expand-lg navbar-dark bg-dark">
        <div class="container">
            <router-link class="navbar-brand" :to="{name: 'home'}">King Of Bots</router-link>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarText" aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarText">
                <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                    <li class="nav-item">
                        <router-link :class="route_name === 'pk_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'pk_index'}">对战</router-link>
                    </li>
                    <li class="nav-item">
                        <router-link :class="route_name === 'record_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'record_index', params: {page: 1}}">对局列表</router-link>
                    </li>
                    <li class="nav-item">
                        <router-link :class="route_name === 'ranklist_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'ranklist_index', params: {page: 1}}">排行榜</router-link>
                    </li>
                    <li class="nav-item">
                        <router-link :class="route_name === 'example_index' ? 'nav-link active' : 'nav-link'" :to="{name: 'example_index'}">游戏规则与样例程序</router-link>
                    </li>
                </ul>
                <ul class="navbar-nav" v-if="!$store.state.user.pullingInfo && $store.state.user.is_login">
                    <li class="nav-item dropdown">
                        <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            {{ $store.state.user.username }}
                        </a>
                        <ul class="dropdown-menu">
                            <li><router-link class="dropdown-item" :to="{name: 'user_bot_index'}">我的Bot</router-link></li>
                            <li><router-link class="dropdown-item" :to="{name:''}" @click="logout">退出</router-link></li>
                        </ul>
                    </li>
                </ul>
                <ul class="navbar-nav" v-if="!$store.state.user.pullingInfo && !$store.state.user.is_login">
                    <li class="nav-item">
                        <router-link :class="route_name === 'user_account_login' ? 'nav-link active' : 'nav-link'" :to="{name: 'user_account_login'}">登录</router-link>
                    </li>
                    <li class="nav-item">
                        <router-link :class="route_name === 'user_account_register' ? 'nav-link active' : 'nav-link'" :to="{name: 'user_account_register'}">注册</router-link>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
</template>


<script>
import { useRoute } from 'vue-router';
import { computed } from '@vue/reactivity';
import { useStore } from 'vuex';

export default {
    setup() {
        const route = useRoute();
        let route_name = computed(() => route.name);

        const store = useStore();
        const logout = () => {
            store.dispatch("logout");
        };

        return {
            route_name,
            logout,
        }
    }
}
</script>

<style scoped>

</style>