package com.kob.backend.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.utils.Game;
import com.kob.backend.consumer.utils.JwtAuthentication;
import com.kob.backend.mapper.UserMapper;
import com.kob.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    private static final ConcurrentHashMap<Integer, WebSocketServer> users =
            new ConcurrentHashMap<>();

    private static final CopyOnWriteArraySet<User> matchpool =
            new CopyOnWriteArraySet<>();

    private static UserMapper userMapper;
    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        WebSocketServer.userMapper = userMapper;
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
            matchpool.remove(this.user);
            System.out.println("disconnected" + this.user.getUsername());
        }
    }

    // 从Client接收消息
    @OnMessage
    public void onMessage(String message, Session session) {
        JSONObject data = JSONObject.parseObject(message);
        String event = data.getString("event");
        if ("start-matching".equals(event)) {
            startMatching();
            System.out.println("start matching " + this.user.getUsername());
        } else if ("stop-matching".equals(event)) {
            stopMatching();
            System.out.println("stop matching " + this.user.getUsername());
        }
    }

    private void startMatching() {
        matchpool.add(this.user);
        if (matchpool.size() >= 2) {
            Iterator<User> it = matchpool.iterator();
            User a = it.next();
            User b = it.next();
            matchpool.remove(a);
            matchpool.remove(b);

            Game game = new Game(13, 14, 20);
            game.createMap();

            JSONObject respA = new JSONObject();
            respA.put("event", "start-matching");
            respA.put("opponent_username", b.getUsername());
            respA.put("opponent_photo", b.getPhoto());
            respA.put("gamemap", game.getGamemap());
            users.get(a.getId()).sendMessage(respA.toJSONString());

            JSONObject respB = new JSONObject();
            respB.put("event", "start-matching");
            respB.put("opponent_username", a.getUsername());
            respB.put("opponent_photo", a.getPhoto());
            respB.put("gamemap", game.getGamemap());
            users.get(b.getId()).sendMessage(respB.toJSONString());
            System.out.println("matched " + a.getUsername() + " " + b.getUsername());
        }
    }

    private void sendMessage(String msg) {
        synchronized (this.session) {
            try {
                this.session.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopMatching() {
        matchpool.remove(this.user);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }
}
