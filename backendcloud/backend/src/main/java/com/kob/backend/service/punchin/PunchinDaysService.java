package com.kob.backend.service.punchin;

import com.alibaba.fastjson2.JSONObject;

public interface PunchinDaysService {
    JSONObject getDays();
    JSONObject addDay(Integer year, Integer month, Integer day, Boolean state);
}
