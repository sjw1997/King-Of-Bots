package com.kob.backend.controller.record;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.pojo.Record;
import com.kob.backend.service.record.GetRecordContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetRecordContentController {
    @Autowired
    private GetRecordContentService getRecordContentService;

    @GetMapping("/api/record/getcontent/{recordId}/")
    public JSONObject getRecordContent(@PathVariable Integer recordId) {
         return getRecordContentService.getRecordContent(recordId);
    }
}
