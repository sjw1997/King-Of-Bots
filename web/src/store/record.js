export default {
    state: {
        is_record: false,
        a_steps: "",
        a_username: "",
        b_steps: "",
        b_username: "",
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
        updateAUsername(state, a_username) {
            state.a_username = a_username;
        },
        updateBSteps(state, b_steps) {
            state.b_steps = b_steps;
        },
        updateBUsername(state, b_username) {
            state.b_username = b_username;
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