<template>
    <ContentField>
        <table class="table table-striped table-hover">
            <thead>
                <tr>
                    <th>蓝方</th>
                    <th>红方</th>
                    <th>对局结果</th>
                    <th>对局时间</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="item in items" :key="item.record.id">
                    <td>
                        <img :src="item.a_photo" alt="" class="user-photo">
                        &nbsp;
                        <span>{{ item.a_username }}</span>
                    </td>
                    <td>
                        <img :src="item.b_photo" alt="" class="user-photo">
                        &nbsp;
                        <span>{{ item.b_username }}</span>
                    </td>
                    <td>{{ item.result }}</td>
                    <td>{{ item.record.createtime }}</td>
                    <td>
                        <router-link :to="{name: 'record_content', params: {'recordId': item.record.id}}">
                            <button type="button" class="btn btn-secondary btn-sm">
                                查看录像
                            </button>
                        </router-link>
                    </td>
                </tr>
            </tbody>
        </table>
        <nav aria-label="...">
            <ul class="pagination" style="float: right">
                <li @click="pull_page" :class="'page-item '+ (current_page === 1 ? 'disabled' : '')">
                    <router-link v-if="current_page > 1" class="page-link" :to="{name: 'record_index', params: {'page': current_page - 1}}">上一页</router-link>
                    <span v-else class="page-link">上一页</span>
                </li>
                <li @click="pull_page" v-for="page in pages" :key="page.number" :class="'page-item ' + page.is_active">
                    <router-link class="page-link" :to="{name: 'record_index', params: {'page': page.number}}">{{ page.number }}</router-link>
                </li>
                <li @click="pull_page" :class="'page-item '+ (current_page === page_count ? 'disabled' : '')">
                    <router-link v-if="current_page < page_count" class="page-link" :to="{name: 'record_index', params: {'page': current_page + 1}}">下一页</router-link>
                    <span v-else class="page-link">下一页</span>
                </li>
            </ul>
        </nav>
    </ContentField>
</template>

<script>
import ContentField from '@/components/ContentField.vue';
import router from '@/router';
import $ from 'jquery';
import { ref } from 'vue';

export default {
    components: {
        ContentField,
    },
    setup() {
        let items = ref([]);
        let current_page = ref(1);
        let page_count = ref(0);
        let pages = ref([]);

        const update_pages = () => {
            let newPages = []
            for (let i = -2; i <= 2; i ++ ) {
                const j = current_page.value + i;
                if (j >= 1 && j <= page_count.value) {
                    newPages.push({
                        number: j,
                        is_active: j === current_page.value ? "active" : ""
                    });
                }
            }
            pages.value = newPages;
        };

        const pull_page = () => {
            current_page.value = parseInt(router.currentRoute.value.params.page)
            $.ajax({
                url: `https://app5212.acapp.acwing.com.cn/api/record/getlist/${current_page.value}/`,
                type: "GET",
                success(resp) {
                    items.value = resp.items;
                    page_count.value = resp.page_count;
                    update_pages();
                }
            });
        };
        pull_page();

        return {
            items,
            pages,
            pull_page,
            current_page,
            page_count,
        }
    }
}
</script>

<style scoped>
.user-photo {
    width: 4vh;
    border-radius: 50%;
}
</style>