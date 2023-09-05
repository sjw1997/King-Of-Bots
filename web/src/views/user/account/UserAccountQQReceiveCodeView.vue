<template>
    <div>

    </div>
</template>

<script>
import $ from 'jquery';
import router from '@/router';
import { useRoute } from 'vue-router';
import { useStore } from 'vuex';

export default {
    setup() {
        const route = useRoute();
        const store = useStore();

        $.ajax({
            url: "https://iamsjw.com/api/user/account/qq/receive_code/",
            type: "GET",
            data: {
                code: route.query.code,
                state: route.query.state,
            },
            success(resp) {
                if (resp.result === "success") {
                    localStorage.setItem("jwt_token", resp.jwt_token);
                    store.commit("updateToken", resp.jwt_token);
                    store.dispatch("getInfo", {
                        success() {
                            router.push({name: 'home'});
                        }
                    });
                } else {
                    router.push({name: 'user_account_login'})
                }
            }
        })
    }
}
</script>

<style scoped>
</style>