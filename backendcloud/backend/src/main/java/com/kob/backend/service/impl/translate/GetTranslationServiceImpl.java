package com.kob.backend.service.impl.translate;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.service.translate.GetTranslationService;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Service
public class GetTranslationServiceImpl implements GetTranslationService {
    @Override
    public JSONObject getTranslation(String lines) {
        String cmd = "python3 /home/acs/translation/translate_one_word.py " + lines;
        StringBuilder res = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String tmp = reader.readLine();
            while (tmp != null) {
                res.append(tmp);
                res.append("\n");
                tmp = reader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONObject resp = new JSONObject();
        resp.put("result", res.toString());
        return resp;
    }
}
