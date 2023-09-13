package com.kob.backend.controller.tomato;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.service.tomato.TomatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
public class TomatoController {
    @Autowired
    private TomatoService tomatoService;

    @GetMapping("/api/tomato/get/days/{userId}/")
    public JSONObject getDays(@PathVariable Integer userId) {
        return tomatoService.getDays(userId);
    }

    @GetMapping("/api/tomato/get/scores/{userId}/")
    public JSONObject getScores(@PathVariable Integer userId) {
        return tomatoService.getScores(userId);
    }

    @GetMapping("/api/tomato/get/current_day_id/")
    public JSONObject getCurrentDayId(@RequestParam MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("userId")));
        Integer year = Integer.parseInt(Objects.requireNonNull(data.getFirst("year")));
        Integer month = Integer.parseInt(Objects.requireNonNull(data.getFirst("month")));
        Integer day = Integer.parseInt(Objects.requireNonNull(data.getFirst("day")));
        return tomatoService.getCurrentDayId(userId, year, month, day);
    }

    @PostMapping("/api/tomato/update/focus_seconds/")
    public JSONObject updateFocusSeconds(@RequestParam MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("userId")));
        Integer tomatoDaysId = Integer.parseInt(Objects.requireNonNull(data.getFirst("tomatoDaysId")));
        Integer focusSeconds = Integer.parseInt(Objects.requireNonNull(data.getFirst("focusSeconds")));
        return tomatoService.updateFocusSeconds(userId, tomatoDaysId, focusSeconds);
    }

    @PostMapping("/api/tomato/update/scores/")
    public JSONObject updateScores(@RequestParam MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("userId")));
        Integer delta = Integer.parseInt(Objects.requireNonNull(data.getFirst("delta")));
        return tomatoService.updateScores(userId, delta);
    }
}
