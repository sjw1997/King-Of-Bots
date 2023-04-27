package com.kob.backend.service.impl.ranklist;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.ranklist.GetRankListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRankListServiceImpl implements GetRankListService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public JSONObject getList(Integer page) {
        final int eachPageSize = 10;
        IPage<User> iPage = new Page<>(page, eachPageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("rating");
        List<User> userList = userMapper.selectPage(iPage, queryWrapper).getRecords();
        for (User user : userList) {
            user.setPassword("");
        }
        JSONObject resp = new JSONObject();
        resp.put("users", userList);

        double cnt = userMapper.selectCount(null);
        resp.put("page_count", Math.ceil(cnt / eachPageSize));
        return resp;
    }
}
