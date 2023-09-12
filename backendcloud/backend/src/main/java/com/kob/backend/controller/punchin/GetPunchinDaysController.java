package com.kob.backend.controller.punchin;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.service.punchin.PunchinDaysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class GetPunchinDaysController {
    @Autowired
    private PunchinDaysService punchinDaysService;

    @GetMapping("/api/punchin/getdays/")
    public JSONObject getDays() {
        return punchinDaysService.getDays();
    }

    @PostMapping("/api/punchin/addday/")
    public JSONObject addDay(@RequestParam MultiValueMap<String, String> data) {
        Integer year = Integer.parseInt(Objects.requireNonNull(data.getFirst("year")));
        Integer month = Integer.parseInt(Objects.requireNonNull(data.getFirst("month")));
        Integer day = Integer.parseInt(Objects.requireNonNull(data.getFirst("day")));
        Boolean state = Boolean.parseBoolean(Objects.requireNonNull(data.getFirst("state")));
        return punchinDaysService.addDay(year, month, day, state);
    }
}
