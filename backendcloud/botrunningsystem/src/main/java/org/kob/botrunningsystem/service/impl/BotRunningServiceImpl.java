package org.kob.botrunningsystem.service.impl;

import org.kob.botrunningsystem.service.BotRunningService;
import org.kob.botrunningsystem.service.impl.utils.BotPool;
import org.springframework.stereotype.Service;

@Service
public class BotRunningServiceImpl implements BotRunningService {
    public static final BotPool botPool = new BotPool();
    @Override
    public String addBot(Integer userId, String botCode, String input) {
        botPool.addBot(userId, botCode, input);
        return "add bot successfully";
    }
}
