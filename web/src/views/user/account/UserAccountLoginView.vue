<template>
    <ContentField>
        <div class="row justify-content-center">
            <div class="col-md-3 col-sm-12">
                <form @submit.prevent="login">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="error-message">{{ error_message }}</div>
                    <button type="submit" class="btn btn-primary">登录</button>
                </form>
                <div style="text-align: center; margin-top: 20px;">
                    <div @click="acwing_login" style="margin-bottom: 5px; cursor: pointer;">
                        <img style="width: 30px;" src="https://cdn.acwing.com/media/article/image/2022/09/06/1_32f001fd2d-acwing_logo.png" alt="">
                        Acwing一键登录
                    </div>
                    <div @click="qq_login" style="cursor: pointer;">
                        <img 
                        style="height: 30px;"
                        src="https://wiki.connect.qq.com/wp-content/uploads/2013/10/03_qq_symbol-1-250x300.png"
                        />
                        QQ账户登录
                    </div>
                    
                    
                </div>
                <div @click="qq_login">
                    
                </div>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue';
import router from '@/router';
import { ref } from 'vue';
import { useStore } from 'vuex';
import $ from 'jquery';

export default {
    components: {
        ContentField,
    },
    setup() {
        let username = ref('');
        let password = ref('');
        let error_message = ref('');

        const store = useStore();
        
        const jwt_token = localStorage.getItem("jwt_token");
        if (jwt_token) {
            store.commit("updateToken", jwt_token);
            store.dispatch("getInfo", {
                success() {
                    router.push({name: 'home'});
                    store.commit("updatePullingInfo", false);
                },
                error() {
                    store.commit("updatePullingInfo", false);
                }
            })
        } else {
            store.commit("updatePullingInfo", false);
        }

        const login = () => {
            error_message.value = "";
            store.dispatch("login", {
                username: username.value,
                password: password.value,
                success() {
                    store.dispatch("getInfo", {
                        success() {
                            router.push({name: 'home'});
                        },
                        error() {
                            error_message.value = "系统异常，请稍后再试";
                        }
                    });
                },
                error() {
                    error_message.value = "系统异常，请稍后再试";
                }
            })
        };

        const acwing_login = () => {
            $.ajax({
                url: "https://iamsjw.com/api/user/account/web/apply_code/",
                type: "GET",
                success(resp) {
                    if (resp.result === "success") {
                        window.location.replace(resp.apply_code_url);
                    }
                }
            });
        };

        const qq_login = () => {
            $.ajax({
                url: "https://iamsjw.com/api/user/account/qq/apply_code/",
                type: "GET",
                success(resp) {
                    if (resp.result === "success") {
                        window.location.replace(resp.apply_code_url);
                    }
                }
            });
        };

        return {
            username,
            password,
            error_message,
            login,
            acwing_login,
            qq_login,
        }
    }
}
</script>

<style scoped>
button {
    width: 100%;
}

.error-message {
    color: red;
    margin-bottom: 5px;
}
</style>