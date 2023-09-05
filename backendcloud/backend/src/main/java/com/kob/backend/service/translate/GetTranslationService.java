package com.kob.backend.service.translate;

import com.alibaba.fastjson2.JSONObject;

public interface GetTranslationService {
    JSONObject getTranslation(String lines);
}
