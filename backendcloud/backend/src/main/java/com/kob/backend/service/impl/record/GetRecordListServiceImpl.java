package com.kob.backend.service.impl.record;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import com.kob.backend.service.record.GetRecordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class GetRecordListServiceImpl implements GetRecordListService {
    @Autowired
    private RecordMapper recordMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public JSONObject getList(Integer page) {
        final int eachPageSize = 10;
        IPage<Record> iPage = new Page<>(page, eachPageSize);
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("createtime");
        List<Record> records = recordMapper.selectPage(iPage, queryWrapper).getRecords();

        JSONObject resp = new JSONObject();
        List<JSONObject> items = new LinkedList<>();
        for (Record record : records) {
            JSONObject item = new JSONObject();
            item.put("record", record);

            User userA = userMapper.selectById(record.getAId());
            User userB = userMapper.selectById(record.getBId());
            item.put("a_photo", userA.getPhoto());
            item.put("a_username", userA.getUsername());
            item.put("b_photo", userB.getPhoto());
            item.put("b_username", userB.getUsername());

            String res = "平局";
            if ("A".equals(record.getLoser())) {
                res = "红方胜";
            } else if ("B".equals(record.getLoser())) {
                res = "蓝方胜";
            }
            item.put("result", res);

            items.add(item);
        }
        resp.put("items", items);
        double cnt = recordMapper.selectCount(null);
        resp.put("page_count", Math.ceil(cnt / eachPageSize));
        return resp;
    }
}
