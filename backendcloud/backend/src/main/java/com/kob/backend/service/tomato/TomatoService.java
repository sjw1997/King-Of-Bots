package com.kob.backend.service.tomato;

import com.alibaba.fastjson2.JSONObject;

public interface TomatoService {
    JSONObject getDays(Integer userId);
    JSONObject getCurrentDayId(Integer userId, Integer year, Integer month, Integer day);
    JSONObject getUserInfo(Integer userId);
    JSONObject updateScores(Integer userId, Integer delta);
    JSONObject updateFocusSeconds(Integer userId, Integer tomatoDaysId, Integer focusSeconds);
    JSONObject updateTargetFocusSeconds(Integer userId, Integer newTargetFocusSeconds);
}
