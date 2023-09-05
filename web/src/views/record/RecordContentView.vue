<template>
    <PlayGround v-if="$store.state.record.pulling_record_content"/>
</template>

<script>
import PlayGround from '@/components/PlayGround.vue'
import { useRoute } from 'vue-router';
import $ from 'jquery';
import { useStore } from 'vuex';
import { onUnmounted } from 'vue';
import router from '@/router';

export default {
    components: {
        PlayGround,
    },
    setup() {
        const store = useStore();
        const route = useRoute();
        const recordId = route.params.recordId;

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

        $.ajax({
            url: `https://iamsjw.com/api/record/getcontent/${recordId}/`,
            type: "GET",
            success(resp) {
                if (resp) {
                    store.commit("updateIsRecord", true);
                    store.commit("updateASteps", resp.record.asteps);
                    store.commit("updateBSteps", resp.record.bsteps);
                    store.commit("updateRecordLoser", resp.record.loser);
                    store.commit("updateGame", {
                        map: stringTo2D(resp.record.map),
                        a_id: resp.record.aid,
                        b_id: resp.record.bid,
                    });
                    store.commit("updateAUsername", resp.a_username);
                    store.commit("updateBUsername", resp.b_username);
                    store.commit("updatePullingRecordContent", true);
                } else {
                    router.push({
                        name: "404"
                    })
                }
            }
        });

        onUnmounted(() => {
            store.commit("updatePullingRecordContent", false);
        })
    }
}
</script>

<style scoped>

</style>