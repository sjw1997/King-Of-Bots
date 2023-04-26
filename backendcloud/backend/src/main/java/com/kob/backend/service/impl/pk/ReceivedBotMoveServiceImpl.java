package com.kob.backend.service.impl.pk;


import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.service.pk.ReceivedBotMoveService;
import org.springframework.stereotype.Service;

@Service
public class ReceivedBotMoveServiceImpl implements ReceivedBotMoveService {
    @Override
    public String receivedBotMove(Integer userId, Integer direction) {
        System.out.println("Received bot move " + userId + " " + direction);
        if (WebSocketServer.users.get(userId) != null) {
            Game game = WebSocketServer.users.get(userId).getGame();
            if (game.getPlayerA().getId().equals(userId)) {
                game.setNextStepA(direction);
            } else {
                game.setNextStepB(direction);
            }
        }
        return "received bot move successfully";
    }
}
