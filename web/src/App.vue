<template>
    <NavBar />
    <router-view />
</template>


<script>
import NavBar from './components/NavBar.vue';
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap.bundle";

export default {
    components: {
        NavBar,
    }
}
</script>

<style>
body {
    background: white url("@/assets/images/background.png") fixed;
    background-size: 100% 100%;
}
</style>
