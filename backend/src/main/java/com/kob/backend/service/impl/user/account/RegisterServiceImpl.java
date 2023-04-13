package com.kob.backend.service.impl.user.account;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import com.kob.backend.service.user.account.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Map<String, String> register(String username, String password, String confrimedPassword) {
        Map<String, String> map = new HashMap<>();
        final String ERROR_MESSAGE = "error_message";

        if (null == username) {
            map.put(ERROR_MESSAGE, "用户名不能为空");
            return map;
        }
        if (null == password || 0 == password.length() ||
            null == confrimedPassword || 0 == confrimedPassword.length()) {
            map.put(ERROR_MESSAGE, "密码不能为空");
            return map;
        }

        username = username.trim();
        if (0 == username.length()) {
            map.put(ERROR_MESSAGE, "用户名不能为空");
            return map;
        }
        if (username.length() > 100) {
            map.put(ERROR_MESSAGE, "用户名长度不能大于100");
            return map;
        }

        if (password.length() > 100) {
            map.put(ERROR_MESSAGE, "密码长度不能大于100");
            return map;
        }

        if (!password.equals(confrimedPassword)) {
            map.put(ERROR_MESSAGE, "两次输入的密码不一致");
            return map;
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        List<User> users = userMapper.selectList(queryWrapper);
        if (null != users && !users.isEmpty()) {
            map.put(ERROR_MESSAGE, "用户名已存在");
            return map;
        }

        String encodedPassword = passwordEncoder.encode(password);
        String photo = "https://cdn.acwing.com/media/user/profile/photo/78150_lg_c7804839c8.jpg";
        userMapper.insert(new User(null, username, encodedPassword, photo));

        map.put(ERROR_MESSAGE, "success");
        return map;
    }
}
