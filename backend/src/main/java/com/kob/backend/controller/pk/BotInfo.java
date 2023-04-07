package com.kob.backend.controller.pk;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pk/")
public class BotInfo {
    @RequestMapping("getbotinfo/")
    public Map<String, String> getBotInfo() {
        List<Map<String, String>> list = new LinkedList<>();
        Map<String, String> bot1 = new HashMap<>();

        bot1.put("name", "apple");
        bot1.put("rating", "1243");

        list.add(bot1);

        return bot1;
    }
}
