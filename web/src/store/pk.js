export default {
    state: {
        status: "matching",  // 'matching'表示不在游戏中，'playing'表示在游戏中
        opponent_username: "",
        opponent_photo: "",
        socket: null,
        gamemap: null,
        a_id: 0,
        b_id: 0,
        gameObject: null,
        loser: "none",
        my_bot_id: -1,
    },
    getters: {
    },
    mutations: {
        updateSocket(state, socket) {
            state.socket = socket;
        },
        updateOpponent(state, opponent) {
            state.opponent_username = opponent.username;
            state.opponent_photo = opponent.photo;
        },
        updateStatus(state, status) {
            state.status = status;
        },
        updateGame(state, game) {
            state.gamemap = game.map;
            state.a_id = game.a_id;
            state.b_id = game.b_id;
        },
        updateGameObject(state, gameObject) {
            state.gameObject = gameObject;
        },
        updateLoser(state, loser) {
            state.loser = loser;
        },
        updateMyBotId(state, my_bot_id) {
            state.my_bot_id = my_bot_id;
        }
    },
    actions: {
    },
    modules: {
    }
  }