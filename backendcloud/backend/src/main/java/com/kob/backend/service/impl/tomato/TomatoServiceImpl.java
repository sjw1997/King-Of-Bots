package com.kob.backend.service.impl.tomato;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.tomato.TomatoDayMapper;
import com.kob.backend.mapper.tomato.TomatoUserMapper;
import com.kob.backend.pojo.tomato.TomatoDay;
import com.kob.backend.pojo.tomato.TomatoUser;
import com.kob.backend.service.tomato.TomatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TomatoServiceImpl implements TomatoService {
    @Autowired
    private TomatoDayMapper tomatoDayMapper;

    @Autowired
    private TomatoUserMapper tomatoUserMapper;


    @Override
    public JSONObject updateFocusSeconds(Integer userId, Integer tomatoDaysId, Integer focusSeconds) {
        TomatoDay tomatoDay = tomatoDayMapper.selectById(tomatoDaysId);

        JSONObject resp = new JSONObject();
        if (null == tomatoDay) {
            resp.put("result", "fail");
            return resp;
        }

        TomatoUser tomatoUser = tomatoUserMapper.selectById(userId);
        if (null == tomatoUser) {
            resp.put("result", "fail");
            resp.put("errorMessage", "不存在该用户");
            return resp;
        }

        Integer before = tomatoDay.getFocusSeconds();
        int after = before + focusSeconds;
        tomatoDay.setFocusSeconds(after);

        resp.put("result", "success");

        final int targetSeconds = tomatoUser.getTargetFocusSeconds();
        if (before < targetSeconds && after >= targetSeconds) {
            tomatoDay.setState(true);
            updateUserScores(userId, 10);
            resp.put("isReachedTarget", true);
        }

        tomatoDayMapper.updateById(tomatoDay);
        return resp;
    }

    @Override
    public JSONObject getDays(Integer userId) {
        JSONObject resp = new JSONObject();
        resp.put("result", "success");

        QueryWrapper<TomatoDay> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<TomatoDay> lst = tomatoDayMapper.selectList(queryWrapper);
        resp.put("items", lst);
        return resp;
    }

    private int findDay(Integer userId, Integer year, Integer month, Integer day) {
        QueryWrapper<TomatoDay> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<TomatoDay> lst = tomatoDayMapper.selectList(queryWrapper);
        for (TomatoDay t : lst) {
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
            TomatoDay t = new TomatoDay(null, year, month, day, false, userId, 0);
            tomatoDayMapper.insert(t);
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
    public JSONObject getUserInfo(Integer userId) {
        TomatoUser tomatoUser = tomatoUserMapper.selectById(userId);
        JSONObject resp = new JSONObject();
        if (null == tomatoUser) {
            resp.put("result", "fail");
            resp.put("error_message", "不存在该用户");
        } else {
            resp.put("result", "success");
            resp.put("username", tomatoUser.getUsername());
            resp.put("scores", tomatoUser.getScores());
            resp.put("targetFocusSeconds", tomatoUser.getTargetFocusSeconds());
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
        if (delta > 0) {
            resp.put("result", "fail");
            resp.put("errorMessage", "想屁吃呢，还想这样加分");
            return resp;
        }
        if (updateUserScores(userId, delta)) {
            resp.put("result", "success");
        } else {
            resp.put("result", "fail");
        }
        return resp;
    }

    @Override
    public JSONObject updateTargetFocusSeconds(Integer userId, Integer newTargetFocusSeconds) {
        JSONObject resp = new JSONObject();
        if (newTargetFocusSeconds < 0) {
            resp.put("result", "fail");
            resp.put("errorMessage", "每日目标时长不能小于0");
            return resp;
        }

        TomatoUser tomatoUser = tomatoUserMapper.selectById(userId);
        if (null == tomatoUser) {
            resp.put("result", "fail");
            resp.put("errorMessage", "不存在该用户");
            return resp;
        }

        tomatoUser.setTargetFocusSeconds(newTargetFocusSeconds);
        tomatoUserMapper.updateById(tomatoUser);
        resp.put("result", "success");
        return resp;
    }
}
