<template>
    <NavBar />
    <router-view />
</template>


<script>
import NavBar from './components/NavBar.vue';
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle";
// import $ from 'jquery';

export default {
    components: {
        NavBar,
    },
    setup() {
        // $.ajax({
        //     url: "http://localhost:3000/user/account/register/",
        //     type: "POST",
        //     data: {
        //         username: 'test_github',
        //         password: "1234567",
        //         confirmedPassword: "1234567",
        //     },
        //     success(resp) {
        //         console.log("success", resp);
        //     },
        //     error(resp) {
        //         console.log("error", resp);
        //     }
        // })
    }
}
</script>

<style>
body {
    background-image: url("@/assets/images/background.png");
    background-size: cover;
}
</style>
