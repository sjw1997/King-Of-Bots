package org.kob.botrunningsystem.service.impl.utils;

import org.joor.Reflect;
import org.kob.botrunningsystem.utils.BotInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class Consumer extends Thread {
    private Bot bot;
    private final static String receivedBotMoveUrl = "http://127.0.0.1:3000/pk/receive/bot/move/";
    private static RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        Consumer.restTemplate = restTemplate;
    }

    public void startTimeout(long timeout, Bot bot) {
        this.bot = bot;
        start();
        try {
            join(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
            interrupt();
        }
    }

    private String addUid(String code, String uid) {
        int index = code.indexOf(" implements org.kob.botrunningsystem.utils.BotInterface");
        return code.substring(0, index) + uid + code.substring(index);
    }

    @Override
    public void run() {
        UUID uuid = UUID.randomUUID();
        String uid = uuid.toString().substring(0, 8);

        BotInterface botInterface = Reflect.compile(
                "org.kob.botrunningsystem.utils.Bot" + uid,
                addUid(bot.getBotCode(), uid)
        ).create().get();

        Integer direction = botInterface.nextMove(bot.getInput());

        System.out.println("direction " + direction);

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", bot.getUserId().toString());
        data.add("direction", direction.toString());
        restTemplate.postForObject(receivedBotMoveUrl, data, String.class);
    }
}
