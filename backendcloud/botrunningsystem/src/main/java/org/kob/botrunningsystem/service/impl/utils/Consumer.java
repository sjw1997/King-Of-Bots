package org.kob.botrunningsystem.service.impl.utils;

import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.UUID;
import java.util.function.Supplier;

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
        int index = code.indexOf(" implements java.util.function.Supplier");
        return code.substring(0, index) + uid + code.substring(index);
    }

    @Override
    public void run() {
        File file = new File("input.txt");
        try (PrintWriter fout = new PrintWriter(file)) {
            fout.println(bot.getInput());
            fout.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Integer direction = 0;
//        UUID uuid = UUID.randomUUID();
//        String uid = uuid.toString().substring(0, 8);
        String uid = String.valueOf(Math.abs(bot.getBotCode().hashCode()));
        String programFileName = "", language = bot.getLanguage(), inputFileName="input.txt";
        if ("Java".equals(language)) {
            programFileName = "Main.java";
            language = "java";
        } else if ("C++".equals(language)) {
            programFileName = String.format("main%s.cpp", uid);
            language = "cpp";
        } else if ("Python3".equals(language)) {
            programFileName = String.format("main%s.py", uid);
            language = "python3";
        }

        file = new File(programFileName);
        try (PrintWriter fout = new PrintWriter(file)) {
            fout.println(bot.getBotCode());
            fout.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        String cmd = String.format("/home/acs/kob/backend/test.sh %s %s %s", language, programFileName, inputFileName);
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String res = reader.readLine();
//            System.out.println(res);
            direction = Integer.parseInt(res);
        } catch (IOException e) {
            e.printStackTrace();
        }

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", bot.getUserId().toString());
        data.add("direction", direction.toString());
        restTemplate.postForObject(receivedBotMoveUrl, data, String.class);
    }
}
