package com.kob.backend.service.user.account.qq;

import com.alibaba.fastjson2.JSONObject;

public interface QQService {
    JSONObject applyCode();
    JSONObject receiveCode(String code, String state);
}
