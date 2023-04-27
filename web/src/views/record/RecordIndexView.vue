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
                        <button @click="open_record_cotent(item.record.id)" type="button" class="btn btn-secondary btn-sm">
                            查看录像
                        </button>
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
import { useStore } from 'vuex';

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

        const store = useStore();
        const pull_page = () => {
            current_page.value = parseInt(router.currentRoute.value.params.page)
            $.ajax({
                url: `http://192.168.0.110:3000/record/getlist/${current_page.value}/`,
                type: "GET",
                success(resp) {
                    items.value = resp.items;
                    page_count.value = resp.page_count;
                    update_pages();
                },
                error(resp) {
                    console.log(resp);
                }
            });
        };
        pull_page();

        const stringTo2D = (map) => {
            let g = [];
            for (let i = 0, k = 0; i < 13; i ++ ) {
                let line = [];
                for (let j = 0; j < 14; j ++ , k ++ ) {
                    if (map[k] === '0') line.push(false);
                    else if(map[k] === '1') line.push(true);
                }
                g.push(line);
            }
            return g;
        };

        const open_record_cotent = (recordId) => {
            for (const item of items.value) {
                if (item.record.id === recordId) {
                    store.commit("updateIsRecord", true);
                    store.commit("updateASteps", item.record.asteps);
                    store.commit("updateBSteps", item.record.bsteps);
                    store.commit("updateRecordLoser", item.record.loser);
                    store.commit("updateGame", {
                        map: stringTo2D(item.record.map),
                        a_id: item.record.aid,
                        b_id: item.record.bid,
                    });
                    store.commit("updateAUsername", item.a_username);
                    store.commit("updateBUsername", item.b_username);
                    router.push({
                        name: "record_content",
                        params: {
                            recordId
                        }
                    });
                }
            }
        };

        return {
            items,
            open_record_cotent,
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