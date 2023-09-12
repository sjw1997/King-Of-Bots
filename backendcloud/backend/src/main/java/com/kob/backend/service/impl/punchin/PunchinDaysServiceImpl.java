package com.kob.backend.service.impl.punchin;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.mapper.PunchinMapper;
import com.kob.backend.pojo.Punchin;
import com.kob.backend.service.punchin.PunchinDaysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PunchinDaysServiceImpl implements PunchinDaysService {
    @Autowired
    private PunchinMapper punchinMapper;

    @Override
    public JSONObject addDay(Integer year, Integer month, Integer day, Boolean state) {
        punchinMapper.insert(new Punchin(null, year, month, day, state));
        JSONObject resp = new JSONObject();
        resp.put("result", "success");
        return resp;
    }

    @Override
    public JSONObject getDays() {
        JSONObject resp = new JSONObject();
        resp.put("result", "success");

        List<Punchin> lst = punchinMapper.selectList(null);
        resp.put("items", lst);
        return resp;
    }
}
