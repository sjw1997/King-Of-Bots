<template>
    <!-- Modal -->
    <div class="modal fade" :id="modal_id" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5" id="exampleModalLabel">{{ modal_title }}</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="title" class="form-label">名称</label>
                        <textarea v-model="bot.title" class="form-control" id="title" rows="1" placeholder="请输入名称"></textarea>
                    </div>
                    <div class="mb-3">
                        <label for="description" class="form-label">简介</label>
                        <textarea v-model="bot.description" class="form-control" id="description" rows="2" placeholder="请输入简介"></textarea>
                    </div>
                    <div class="mb-3">
                        <div class="row">
                            <div class="col-6">
                                <label for="content" class="form-label">代码</label>
                            </div>
                            <div class="col-6">
                                <select v-model="bot.language" style="width: 50%; float: right;" class="form-select form-select-sm">
                                    <option value="C++">C++</option>
                                    <option value="Java">Java</option>
                                    <option value="Python3">Python3</option>
                                </select>
                            </div>
                        </div>
                        <VAceEditor
                            v-model:value="bot.content"
                            lang="c_cpp"
                            theme="textmate"
                            style="height: 300px"
                            :options="{
                            enableBasicAutocompletion: true, //启用基本自动完成
                            enableSnippets: true, // 启用代码段
                            enableLiveAutocompletion: true, // 启用实时自动完成
                            fontSize: 14, //设置字号
                            tabSize: 4, // 标签大小
                            showPrintMargin: false, //去除编辑器里的竖线
                            highlightActiveLine: true,
                            }"
                        />               
                    </div>
                </div>
                <div class="modal-footer">
                    <span class="error-message">{{ error_message }}</span>
                    <button @click="submit_bot" type="button" class="btn btn-primary">确定</button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { reactive, ref } from 'vue';
import $ from 'jquery';
import { useStore } from 'vuex';
import { Modal } from 'bootstrap/dist/js/bootstrap.bundle';
import { VAceEditor } from 'vue3-ace-editor';
import ace from 'ace-builds';
import 'ace-builds/src-noconflict/mode-c_cpp';
import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-chrome';
import 'ace-builds/src-noconflict/ext-language_tools';

ace.config.set(
"basePath",
"https://cdn.jsdelivr.net/npm/ace-builds@" +
require("ace-builds").version +
"/src-noconflict/");

export default {

    components: {
        VAceEditor,
    },
    props: {
        modal_id: {
            type: String,
            required: true,
        },
        modal_title: {
            type: String,
            required: true,
        },
        modal_bot: {
            type: Object,
            required: false,
        },
    },
    setup(props, context) {
        const bot = reactive({
            id: "",
            description: "",
            language: "C++",
            content: "",
            title: "",
        });
        let error_message = ref("");

        if (props.modal_bot) {
            bot.id = JSON.parse(JSON.stringify(props.modal_bot.id));
            bot.description = JSON.parse(JSON.stringify(props.modal_bot.description));
            bot.content = JSON.parse(JSON.stringify(props.modal_bot.content));
            bot.title = JSON.parse(JSON.stringify(props.modal_bot.title));
            bot.language = JSON.parse(JSON.stringify(props.modal_bot.language));
        }

        const store = useStore();
        const submit_bot = () => {
            if (props.modal_title === "创建Bot") {
                error_message.value = "";
                $.ajax({
                    url: "https://iamsjw.com/api/user/bot/add/",
                    type: "POST",
                    data: {
                        title: bot.title,
                        content: bot.content,
                        description: bot.description,
                        language: bot.language,
                    },
                    headers: {
                        Authorization: `Bearer ${store.state.user.token}`
                    },
                    success(resp) {
                        if (resp.error_message === "success") {
                            Modal.getInstance("#"+props.modal_id).hide();
                            context.emit("refresh_bots");
                        } else {
                            error_message.value = resp.error_message;
                        }
                    }
                });
            } else {
                error_message.value = "";
                $.ajax({
                    url: "https://iamsjw.com/api/user/bot/update/",
                    type: "POST",
                    data: {
                        bot_id: bot.id,
                        title: bot.title,
                        content: bot.content,
                        description: bot.description,
                        language: bot.language,
                    },
                    headers: {
                        Authorization: `Bearer ${store.state.user.token}`
                    },
                    success(resp) {
                        if (resp.error_message === "success") {
                            Modal.getInstance("#"+props.modal_id).hide();
                            context.emit("refresh_bots");
                        } else {
                            error_message.value = resp.error_message;
                        }
                    }
                });
            }
        }

        return {
            bot,
            submit_bot,
            error_message,
        }
    }
}

</script>

<style scoped>
.error-message {
    color: red;
}
</style>