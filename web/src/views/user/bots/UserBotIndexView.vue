<template>
    <ContentField>
        我的Bot
    </ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue';
import $ from 'jquery';
import { useStore } from 'vuex';

export default {
    components: {
        ContentField,
    },
    setup() {
        const store = useStore();
        
        // $.ajax({
        //     url: "http://localhost:3000/user/bot/add/",
        //     type: "POST",
        //     data: {
        //         title: "我的Bot-github",
        //         content: "daima",
        //     },
        //     headers: {
        //         Authorization: `Bearer ${store.state.user.token}`
        //     },
        //     success(resp) {
        //         console.log(resp);
        //     },
        //     error(resp) {
        //         console.log(resp);
        //     }
        // })

        // $.ajax({
        //     url: "http://localhost:3000/user/bot/remove/",
        //     type: "POST",
        //     data: {
        //         bot_id: 4
        //     },
        //     headers: {
        //         Authorization: `Bearer ${store.state.user.token}`
        //     },
        //     success(resp) {
        //         console.log(resp);
        //     },
        //     error(resp) {
        //         console.log(resp);
        //     }
        // })

        // $.ajax({
        //     url: "http://localhost:3000/user/bot/update/",
        //     type: "POST",
        //     data: {
        //         bot_id: 2,
        //         title: "ce shi update",
        //         content: "ceshi",
        //     },
        //     headers: {
        //         Authorization: `Bearer ${store.state.user.token}`
        //     },
        //     success(resp) {
        //         console.log(resp);
        //     },
        //     error(resp) {
        //         console.log(resp);
        //     }
        // })

        $.ajax({
            url: "http://localhost:3000/user/bot/getlist/",
            type: "GET",
            headers: {
                Authorization: `Bearer ${store.state.user.token}`
            },
            success(resp) {
                console.log(resp);
            },
            error(resp) {
                console.log(resp);
            }
        })
    }
}
</script>

<style scoped>

</style>