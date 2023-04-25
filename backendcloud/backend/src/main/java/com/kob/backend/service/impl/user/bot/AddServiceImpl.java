package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class AddServiceImpl implements AddService {
    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> add(Map<String, String> data) {
        String title = data.get("title");
        String description = data.get("description");
        String content = data.get("content");

        Map<String, String> map = new HashMap<>();
        final String ERROR_MESSAGE = "error_message";

        if (null == title || 0 == title.length()) {
            map.put(ERROR_MESSAGE, "Bot标题不能为空");
            return map;
        } else if (title.length() > 100) {
            map.put(ERROR_MESSAGE, "Bot标题长度不能大于100");
            return map;
        }

        if (null == description || 0 == description.length()) {
            description = "这个用户太懒，什么都没写";
        } else if (description.length() > 300) {
            map.put(ERROR_MESSAGE, "Bot描述长度不能大于300");
            return map;
        }

        if (null == content || 0 == content.length()) {
            map.put(ERROR_MESSAGE, "Bot代码不能为空");
            return map;
        } else if (content.length() > 10000) {
            map.put(ERROR_MESSAGE, "Bot代码长度不能大于10000");
            return map;
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) usernamePasswordAuthenticationToken.getPrincipal();
        User user = loginUser.getUser();

        Date time = new Date();
        Bot bot = new Bot(
                null,
                user.getId(),
                title,
                description,
                content,
                time,
                time
        );
        botMapper.insert(bot);

        map.put(ERROR_MESSAGE, "success");
        return map;
    }
}
