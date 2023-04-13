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
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue';
import router from '@/router';
import { ref } from 'vue';
import { useStore } from 'vuex';

export default {
    components: {
        ContentField,
    },
    setup() {
        let username = ref('');
        let password = ref('');
        let error_message = ref('');

        const store = useStore();
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
                        error(resp) {
                            console.log(resp);
                        }
                    });
                },
                error() {
                    error_message.value = "用户名或密码错误";
                }
            })
        };

        return {
            username,
            password,
            error_message,
            login,
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