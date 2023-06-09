import { createRouter, createWebHistory } from 'vue-router'
import PkIndexView from '@/views/pk/PkIndexView';
import RecordIndexView from '@/views/record/RecordIndexView';
import RecordContentView from '@/views/record/RecordContentView';
import RanklistIndexView from '@/views/ranklist/RanklistIndexView';
import UserBotIndexView from '@/views/user/bots/UserBotIndexView';
import NotFoundView from '@/views/error/NotFoundView';
import UserAccountLoginView from '@/views/user/account/UserAccountLoginView';
import UserAccountRegisterView from '@/views/user/account/UserAccountRegisterView';
import UserAccountAcWingWebReceiveCodeView from '@/views/user/account/UserAccountAcWingWebReceiveCodeView';
import UserAccountQQReceiveCodeView from '@/views/user/account/UserAccountQQReceiveCodeView';
import ExampleIndexView from '@/views/example/ExampleIndexView';
import store from '@/store';

const routes = [
  {
    path: "/",
    name: "home",
    redirect: "/pk/",
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/pk/",
    name: "pk_index",
    component: PkIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/example/",
    name: "example_index",
    component: ExampleIndexView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/record/index/:page/",
    name: "record_index",
    component: RecordIndexView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/record/content/:recordId/",
    name: "record_content",
    component: RecordContentView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/ranklist/index/:page/",
    name: "ranklist_index",
    component: RanklistIndexView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/user/bot/",
    name: "user_bot_index",
    component: UserBotIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/user/account/login/",
    name: "user_account_login",
    component: UserAccountLoginView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/user/account/register/",
    name: "user_account_register",
    component: UserAccountRegisterView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/user/account/web/receive_code/",
    name: "user_account_acwing_web_receive_code",
    component: UserAccountAcWingWebReceiveCodeView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/user/account/qq/receive_code/",
    name: "user_account_qq_receive_code",
    component: UserAccountQQReceiveCodeView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/404/",
    name: "404",
    component: NotFoundView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/:catchAll(.*)",
    redirect: '/404/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const jwt_token = localStorage.getItem("jwt_token");
  if (jwt_token) {
    store.commit("updateToken", jwt_token);
    store.dispatch("getInfo", {
      success() {
        store.commit("updatePullingInfo", false);
      },
      error() {
        localStorage.removeItem('jwt_token');
        store.commit("updatePullingInfo", false);
      }
    })
  }

  if (store.state.user.is_login || !to.meta.requestAuth) {
    store.commit("updatePullingInfo", false);
    next();
  } else {
    store.commit("updatePullingInfo", false);
    next({name: 'user_account_login'});
  }
})

export default router
