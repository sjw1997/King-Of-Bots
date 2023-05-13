package com.kob.backend.service.impl.record;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import com.kob.backend.service.record.GetRecordContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetRecordContentServiceImpl implements GetRecordContentService {
    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public JSONObject getRecordContent(Integer recordId) {
        Record record = recordMapper.selectById(recordId);
        if (null == record) {
            return null;
        }

        JSONObject resp = new JSONObject();
        User userA = userMapper.selectById(record.getAId());
        User userB = userMapper.selectById(record.getBId());
        resp.put("record", record);
        resp.put("a_username", userA.getUsername());
        resp.put("b_username", userB.getUsername());
        return resp;
    }
}
