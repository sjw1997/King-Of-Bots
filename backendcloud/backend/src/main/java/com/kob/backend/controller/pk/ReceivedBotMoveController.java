package com.kob.backend.controller.pk;

import com.kob.backend.service.pk.ReceivedBotMoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class ReceivedBotMoveController {
    @Autowired
    private ReceivedBotMoveService receivedBotMoveService;

    @PostMapping("/pk/receive/bot/move/")
    public String receivedBotMove(@RequestParam MultiValueMap<String, String> data) {
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user_id")));
        Integer direction = Integer.parseInt(Objects.requireNonNull(data.getFirst("direction")));
        System.out.println("controller - received direction " + userId + " " + direction);
        return receivedBotMoveService.receivedBotMove(userId, direction);
    }
}
