package com.kob.backend.service.impl.tomato;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.tomato.TomatoDaysMapper;
import com.kob.backend.mapper.tomato.TomatoUserMapper;
import com.kob.backend.pojo.tomato.TomatoDays;
import com.kob.backend.pojo.tomato.TomatoUser;
import com.kob.backend.service.tomato.TomatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TomatoServiceImpl implements TomatoService {
    @Autowired
    private TomatoDaysMapper tomatoDaysMapper;

    @Autowired
    private TomatoUserMapper tomatoUserMapper;


    @Override
    public JSONObject updateFocusSeconds(Integer userId, Integer tomatoDaysId, Integer focusSeconds) {
        TomatoDays tomatoDays = tomatoDaysMapper.selectById(tomatoDaysId);

        JSONObject resp = new JSONObject();
        if (null == tomatoDays) {
            resp.put("result", "fail");
            return resp;
        }

        Integer before = tomatoDays.getFocusSeconds();
        int after = before + focusSeconds;
        tomatoDays.setFocusSeconds(after);

        resp.put("result", "success");

        final int targetSeconds = 60;
        if (before < targetSeconds && after >= targetSeconds) {
            tomatoDays.setState(true);
            updateUserScores(userId, 10);
            resp.put("isReachedTarget", true);
        }

        tomatoDaysMapper.updateById(tomatoDays);
        return resp;
    }

    @Override
    public JSONObject getDays(Integer userId) {
        JSONObject resp = new JSONObject();
        resp.put("result", "success");

        QueryWrapper<TomatoDays> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<TomatoDays> lst = tomatoDaysMapper.selectList(queryWrapper);
        resp.put("items", lst);
        return resp;
    }

    private int findDay(Integer userId, Integer year, Integer month, Integer day) {
        QueryWrapper<TomatoDays> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<TomatoDays> lst = tomatoDaysMapper.selectList(queryWrapper);
        for (TomatoDays t : lst) {
            if (Objects.equals(t.getYear(), year) && Objects.equals(t.getMonth(), month) && Objects.equals(t.getDay(), day)) {
                return t.getId();
            }
        }
        return -1;
    }

    @Override
    public JSONObject getCurrentDayId(Integer userId, Integer year, Integer month, Integer day) {
        JSONObject resp = new JSONObject();
        int id = findDay(userId, year, month, day);
        if (id == -1) {
            TomatoDays t = new TomatoDays(null, year, month, day, false, userId, 0);
            tomatoDaysMapper.insert(t);
            id = findDay(userId, year, month, day);
            if (id == -1) {
                System.out.println("insert new tomato day failed");
                resp.put("result", "fail");
                return resp;
            }
        }

        resp.put("result", "success");
        resp.put("id", id);
        return resp;
    }

    @Override
    public JSONObject getScores(Integer userId) {
        TomatoUser tomatoUser = tomatoUserMapper.selectById(userId);
        JSONObject resp = new JSONObject();
        if (null == tomatoUser) {
            resp.put("result", "fail");
            resp.put("error_message", "不存在该用户");
        } else {
            resp.put("result", "success");
            resp.put("scores", tomatoUser.getScores());
        }
        return resp;
    }

    private boolean updateUserScores(Integer userId, Integer delta) {
        TomatoUser tomatoUser = tomatoUserMapper.selectById(userId);
        if (null == tomatoUser) {
            return false;
        }
        tomatoUser.setScores(tomatoUser.getScores() + delta);
        tomatoUserMapper.updateById(tomatoUser);
        return true;
    }

    @Override
    public JSONObject updateScores(Integer userId, Integer delta) {
        JSONObject resp = new JSONObject();
        if (updateUserScores(userId, delta)) {
            resp.put("result", "success");
        } else {
            resp.put("result", "fail");
        }
        return resp;
    }
}
