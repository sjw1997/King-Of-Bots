export default {
    state: {
        is_record: false,
        a_steps: "",
        b_steps: "",
        record_loser: "",
    },
    getters: {
    },
    mutations: {
        updateIsRecord(state, is_record) {
            state.is_record = is_record;
        },
        updateASteps(state, a_steps) {
            state.a_steps = a_steps;
        },
        updateBSteps(state, b_steps) {
            state.b_steps = b_steps;
        },
        updateRecordLoser(state, record_loser) {
            state.record_loser = record_loser;
        },
    },
    actions: {
    },
    modules: {
    }
  }