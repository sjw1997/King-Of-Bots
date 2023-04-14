package com.kob.backend.service.impl.user.bot;

import com.kob.backend.mapper.BotMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import com.kob.backend.service.impl.utils.UserDetailsImpl;
import com.kob.backend.service.user.bot.RemoveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RemoveServiceImpl implements RemoveService {
    @Autowired
    private BotMapper botMapper;

    @Override
    public Map<String, String> remove(Map<String, String> data) {
        Map<String, String> map = new HashMap<>();
        final String ERROR_MESSAGE = "error_message";

        if (null == data.get("bot_id")) {
            map.put(ERROR_MESSAGE, "Bot id不能为空");
            return map;
        }

        Integer bot_id = Integer.parseInt(data.get("bot_id"));
        Bot bot = botMapper.selectById(bot_id);
        if (null == bot) {
            map.put(ERROR_MESSAGE, "该Bot不存在或已被删除");
            return map;
        }

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl loginUser = (UserDetailsImpl) usernamePasswordAuthenticationToken.getPrincipal();
        User user = loginUser.getUser();

        if (!user.getId().equals(bot.getUserId())) {
            map.put(ERROR_MESSAGE, "您没有权限删除该Bot");
            return map;
        }

        botMapper.deleteById(bot_id);
        map.put(ERROR_MESSAGE, "success");
        return map;
    }
}
