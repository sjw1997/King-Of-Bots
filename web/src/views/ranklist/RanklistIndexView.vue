<template>
    <ContentField>
        <table class="table table-striped table-hover">
            <thead>
                <tr>
                    <th>玩家</th>
                    <th>天梯分</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="user in users" :key="user.id">
                    <td>
                        <img :src="user.photo" alt="" class="user-photo">
                        &nbsp;
                        <span>{{ user.username }}</span>
                    </td>
                    <td>{{ user.rating }}</td>
                </tr>
            </tbody>
        </table>
        <nav aria-label="...">
            <ul class="pagination" style="float: right">
                <li @click="pull_page" :class="'page-item '+ (current_page === 1 ? 'disabled' : '')">
                    <router-link v-if="current_page > 1" class="page-link" :to="{name: 'ranklist_index', params: {'page': current_page - 1}}">上一页</router-link>
                    <span v-else class="page-link">上一页</span>
                </li>
                <li @click="pull_page" v-for="page in pages" :key="page.number" :class="'page-item ' + page.is_active">
                    <router-link class="page-link" :to="{name: 'ranklist_index', params: {'page': page.number}}">{{ page.number }}</router-link>
                </li>
                <li @click="pull_page" :class="'page-item '+ (current_page === page_count ? 'disabled' : '')">
                    <router-link v-if="current_page < page_count" class="page-link" :to="{name: 'ranklist_index', params: {'page': current_page + 1}}">下一页</router-link>
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
        let users = ref([]);
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
                url: `http://192.168.0.110:3000/ranklist/getlist/${current_page.value}/`,
                type: "GET",
                success(resp) {
                    users.value = resp.users;
                    page_count.value = resp.page_count;
                    update_pages();
                },
                error(resp) {
                    console.log(resp);
                }
            });
        };
        pull_page();

        return {
            users,
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