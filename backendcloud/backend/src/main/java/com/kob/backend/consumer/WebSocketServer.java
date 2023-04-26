package com.kob.backend.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.BotMapper;
import com.kob.backend.mapper.RecordMapper;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@ServerEndpoint("/websocket/{token}")  // 注意不要以'/'结尾
public class WebSocketServer {
    private Session session = null;

    private User user;
    private Game game;

    public static final ConcurrentHashMap<Integer, WebSocketServer> users =
            new ConcurrentHashMap<>();

    private static UserMapper userMapper;
    public static RecordMapper recordMapper;
    private static BotMapper botMapper;
    public static RestTemplate restTemplate;
    private final static String addPlayerUrl = "http://127.0.0.1:3001/player/add/";
    private final static String removePlayerUrl = "http://127.0.0.1:3001/player/remove/";

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
    }

    @Autowired
    public void setRecordMapper(RecordMapper recordMapper) {
        WebSocketServer.recordMapper = recordMapper;
    }
    @Autowired
    public void setBotMapper(BotMapper botMapper) {
        WebSocketServer.botMapper = botMapper;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        WebSocketServer.restTemplate = restTemplate;
    }

    // 建立连接
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        this.session = session;

        Integer userId = JwtAuthentication.parseUserId(token);
        this.user = userMapper.selectById(userId);

        if (this.user != null) {
            users.put(userId, this);
            System.out.println("connected " + user.getUsername());
        } else {
            this.session.close();
        }
    }

    // 关闭链接
    @OnClose
    public void onClose() {
        if (this.user != null) {
            users.remove(user.getId());
            stopMatching();
            System.out.println("disconnected" + this.user.getUsername());
        }
    }

    // 从Client接收消息
    @OnMessage
    public void onMessage(String message, Session session) {
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        System.out.println("received message");
        if ("start-matching".equals(event)) {
            Integer botId = data.getInteger("bot_id");
            startMatching(botId);
            System.out.println("start matching " + this.user.getUsername() + " " + botId);
        } else if ("stop-matching".equals(event)) {
            stopMatching();
            System.out.println("stop matching " + this.user.getUsername());
        } else if ("move".equals(event)) {
            move(data.getInteger("direction"));
            System.out.println("move direction " + this.user.getUsername());
        }
    }

    private void move(int direction) {
        if (user.getId().equals(game.getPlayerA().getId())) {
            if (game.getPlayerA().getBotId().equals(-1)) {
                game.setNextStepA(direction);
            }
        } else if (user.getId().equals(game.getPlayerB().getId())) {
            if (game.getPlayerB().getBotId().equals(-1)) {
                game.setNextStepB(direction);
            }
        }
    }

    public Game getGame() {
        return game;
    }

    public static void startGame(Integer aId, Integer aBotId, Integer bId, Integer bBotId) {
        User a = userMapper.selectById(aId), b = userMapper.selectById(bId);
        Bot botA = botMapper.selectById(aBotId), botB = botMapper.selectById(bBotId);
        Game game = new Game(
                13,
                14,
                20,
                a.getId(),
                botA,
                b.getId(),
                botB
        );
        game.createMap();
        if (WebSocketServer.users.get(a.getId()) != null) {
            WebSocketServer.users.get(a.getId()).game = game;
        }
        if (WebSocketServer.users.get(b.getId()) != null) {
            WebSocketServer.users.get(b.getId()).game = game;
        }
        game.start();

        JSONObject resp = new JSONObject();
        resp.put("map", game.getGamemap());
        resp.put("a_id", a.getId());
        resp.put("b_id", b.getId());

        JSONObject respA = new JSONObject();
        respA.put("event", "start-matching");
        respA.put("opponent_username", b.getUsername());
        respA.put("opponent_photo", b.getPhoto());
        respA.put("game", resp);
        if (WebSocketServer.users.get(a.getId()) != null) {
            WebSocketServer.users.get(a.getId()).sendMessage(respA.toJSONString());
        }

        JSONObject respB = new JSONObject();
        respB.put("event", "start-matching");
        respB.put("opponent_username", a.getUsername());
        respB.put("opponent_photo", a.getPhoto());
        respB.put("game", resp);
        if (WebSocketServer.users.get(b.getId()) != null) {
            WebSocketServer.users.get(b.getId()).sendMessage(respB.toJSONString());
        }
        System.out.println("matched " + a.getUsername() + " " + b.getUsername());
    }

    private void startMatching(Integer botId) {
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", user.getId().toString());
        data.add("rating", user.getRating().toString());
        data.add("bot_id", botId.toString());
        restTemplate.postForObject(WebSocketServer.addPlayerUrl, data, String.class);
    }

    private void stopMatching() {
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", user.getId().toString());
        restTemplate.postForObject(WebSocketServer.removePlayerUrl, data, String.class);
    }

    public void sendMessage(String msg) {
        synchronized (this.session) {
            try {
                this.session.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }
}
