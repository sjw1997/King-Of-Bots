package com.kob.backend.controller.translate;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.service.translate.GetTranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetTranslationController {
    @Autowired
    private GetTranslationService getTranslationService;

    @GetMapping("/api/translate/")
    public JSONObject getTranslation(@RequestParam MultiValueMap<String, String> data) {
        String lines = data.getFirst("lines");
        return getTranslationService.getTranslation(lines);
    }
}
