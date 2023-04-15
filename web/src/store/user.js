import $ from 'jquery';

export default {
    state: {
        id: "",
        username: "",
        photo: "",
        is_login: false,
        token: "",
        pullingInfo: true,  // 正在拉取信息
    },
    getters: {
    },
    mutations: {
        updateUser(state, user) {
            state.id = user.id;
            state.username = user.username;
            state.photo = user.photo;
            state.is_login = user.is_login;
        },
        updateToken(state, token) {
            state.token = token;
        },
        updatePullingInfo(state, pullingInfo) {
            state.pullingInfo = pullingInfo;
        }
    },
    actions: {
        login(context, data) {
            $.ajax({
                url: "http://192.168.0.110:3000/user/account/token/",
                type: "POST",
                data: {
                    username: data.username,
                    password: data.password,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        localStorage.setItem("jwt_token", resp.token);
                        context.commit("updateToken", resp.token);
                        data.success(resp);
                    } else {
                        data.error(resp);
                    }
                },
                error(resp) {
                    data.error(resp);
                }
            });
        },
        getInfo(context, data) {
            $.ajax({
                url: "http://192.168.0.110:3000/user/account/info/",
                type: "GET",
                headers: {
                    'Authorization': "Bearer " + context.state.token
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        context.commit("updateUser", {
                            ...resp,
                            is_login: true,
                        });
                        data.success(resp);
                    } else {
                        data.error(resp);
                    }
                },
                error(resp) {
                    data.error(resp);
                }
            });
        },
        logout(context) {
            context.commit("updateToken", "");
            context.commit("updateUser", {
                id: "",
                username: "",
                photo: "",
                is_login: false,
            });
            localStorage.removeItem("jwt_token");
            location.reload();
        }
    },
    modules: {
    }
}