<template>
    <ContentField>
        <div class="row justify-content-center">
            <div class="col-md-3 col-sm-12">
                <form @submit.prevent="register">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="mb-3">
                        <label for="confirmedPassword" class="form-label">确认密码</label>
                        <input v-model="confirmedPassword" type="password" class="form-control" id="confirmedPassword" placeholder="请再次输入密码">
                    </div>
                    <div class="error-message">{{ error_message }}</div>
                    <button type="submit" class="btn btn-primary">注册</button>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue';
import router from '@/router';
import { ref } from 'vue';
import $ from 'jquery';
import { useStore } from 'vuex';

export default {
    components: {
        ContentField,
    },
    setup() {
        let username = ref('');
        let password = ref('');
        let confirmedPassword = ref('');
        let error_message = ref('');

        const store = useStore();
        const register = () => {
            error_message.value = "";
            $.ajax({
                url: "http://localhost:3000/user/account/register/",
                type: "POST",
                data: {
                    username: username.value,
                    password: password.value,
                    confirmedPassword: confirmedPassword.value,
                },
                success(resp) {
                    if (resp.error_message === "success") {
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
                    } else {
                        error_message.value = resp.error_message;
                    }
                },
                error() {
                    error_message.value = "系统异常，请稍后再试"
                }
            })
        };

        return {
            username,
            password,
            error_message,
            confirmedPassword,
            register,
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