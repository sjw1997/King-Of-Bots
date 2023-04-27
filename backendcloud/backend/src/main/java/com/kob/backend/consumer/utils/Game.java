package com.kob.backend.consumer.utils;

import com.alibaba.fastjson2.JSONObject;
import com.kob.backend.consumer.WebSocketServer;
import com.kob.backend.pojo.Bot;
import com.kob.backend.pojo.Record;
import com.kob.backend.pojo.User;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread {
    private final int rows;
    private final int cols;
    private final int innerWallsCount;
    private final boolean[][] gamemap;
    private final int[] dx = {-1, 0, 1, 0};
    private final int[] dy = {0, 1, 0, -1};
    private final Player playerA;
    private final Player playerB;
    private Integer nextStepA = null;
    private Integer nextStepB = null;
    private ReentrantLock lock = new ReentrantLock();
    private String status = "playing";
    private String loser = "";
    private final static String addBotUrl = "http://127.0.0.1:3002/bot/add/";



    public Game(int rows, int cols, int innerWallsCount, int aId, Bot botA, int bId, Bot botB) {
        this.rows = rows;
        this.cols = cols;
        this.innerWallsCount = innerWallsCount;
        this.gamemap = new boolean[rows][cols];
        playerA = new Player(
                aId,
                botA == null ? -1 : botA.getId(),
                botA == null ? "" : botA.getContent(),
                rows - 2,
                1,
                new ArrayList<>()
        );
        playerB = new Player(
                bId,
                botB == null ? -1 : botB.getId(),
                botB == null ? "" : botB.getContent(),
                1,
                cols - 2,
                new ArrayList<>()
        );
    }

    public boolean[][] getGamemap() {
        return gamemap;
    }

    public Player getPlayerA() {
        return playerA;
    }

    public Player getPlayerB() {
        return playerB;
    }

    public void createMap() {
        for (int i = 0; i < 1000; i ++ ) {
            if (createWalls()) {
                break;
            }
        }
    }

    private String getMapString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < rows; i ++ ) {
            for (int j = 0; j < cols; j ++ ) {
                res.append(gamemap[i][j] ? 1 : 0);
            }
        }
        return res.toString();
    }

    private boolean createWalls() {
        // 初始化地图，初始地图中没有障碍物
        for (int i = 0; i < rows; i ++ ) {
            Arrays.fill(gamemap[i], false);
        }

        // 给四周加上障碍物
        for (int r = 0; r < rows; r ++ ) {
            gamemap[r][0] = gamemap[r][cols - 1] = true;
        }
        for (int c = 0; c < cols; c ++ ) {
            gamemap[0][c] = gamemap[rows - 1][c] = true;
        }

        // 创建随机障碍物
        Random random = new Random();
        for (int i = 0; i < innerWallsCount / 2; i ++ ) {
            for (int j = 0; j < 1000; j ++ ) {
                int r = random.nextInt(rows);
                int c = random.nextInt(cols);
                if (gamemap[r][c] || gamemap[rows - 1 - r][cols - 1 - c]) {
                    continue;
                }
                if (r == rows - 2 && 1 == c ||
                    1 == r && c == cols - 2) {
                    continue;
                }
                gamemap[r][c] = gamemap[rows - 1 - r][cols - 1 - c] = true;
                break;
            }
        }

        return check_connectivity(rows - 2, 1, 1, cols - 2);
    }

    private boolean check_connectivity(int sx, int sy, int tx, int ty) {
        if (sx == tx && sy == ty) {
            return true;
        }

        gamemap[sx][sy] = true;
        for (int i = 0; i < 4; i ++ ) {
            int a = sx + dx[i], b = sy + dy[i];
            if (a >= 0 && a < rows && b >= 0 && b < cols && !gamemap[a][b]) {
                if (check_connectivity(a, b, tx, ty)) {
                    gamemap[sx][sy] = false;
                    return true;
                }
            }
        }
        gamemap[sx][sy] = false;
        return false;
    }

    public void setNextStepA(int direction) {
        lock.lock();
        try {
            nextStepA = direction;
        } finally {
            lock.unlock();
        }
    }

    public void setNextStepB(int direction) {
        lock.lock();
        try {
            nextStepB = direction;
        } finally {
            lock.unlock();
        }
    }

    // 检测目标位置是否合法，没有撞到两条蛇的身体和障碍物
    private boolean checkValid(List<Cell> cellsA, List<Cell> cellsB) {
        int n = cellsA.size();
        Cell cell = cellsA.get(n - 1);

        if (gamemap[cell.x][cell.y]) {
            return false;
        }

        for (int i = 0; i < n - 1; i ++ ) {
            if (cell.x == cellsB.get(i).x && cell.y == cellsB.get(i).y) {
                return false;
            }
        }

        for (int i = 0; i < n - 1; i ++ ) {
            if (cell.x == cellsA.get(i).x && cell.y == cellsA.get(i).y) {
                return false;
            }
        }
        return true;
    }

    // 判断两名玩家的下一步操作是否合法
    private void judge() {
        List<Cell> cellsA = playerA.getCells();
        List<Cell> cellsB = playerB.getCells();

        boolean validA = checkValid(cellsA, cellsB);
        boolean validB = checkValid(cellsB, cellsA);

        if (!validA || !validB) {
            status = "finished";
            if (!validA && !validB) {
                loser = "all";
            } else if (!validA) {
                loser = "A";
            } else {
                loser = "B";
            }
        }
    }

    private String getInput(Player player) {
        Player you;
        if (player.getId().equals(playerA.getId())) {
            you = playerB;
        } else {
            you = playerA;
        }

        return getMapString() + "#" +
                player.getSx() + "#" +
                player.getSy() + "#(" +
                player.getStepsString() + ")#" +
                you.getSx() + "#" +
                you.getSy() + "#(" +
                you.getStepsString() + ")";
    }

    private void sendBotCode(Player player) {
        if (player.getBotId().equals(-1)) {
            return;
        }

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id", player.getId().toString());
        data.add("bot_code", player.getBotCode());
        data.add("input", getInput(player));
        WebSocketServer.restTemplate.postForObject(addBotUrl, data, String.class);
    }

    private boolean nextStep() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sendBotCode(playerA);
        sendBotCode(playerB);

        for (int i = 0; i < 50; i ++ ) {
            try {
                Thread.sleep(100);
                lock.lock();
                try {
                    if (nextStepA != null && nextStepB != null) {
                        playerA.getSteps().add(nextStepA);
                        playerB.getSteps().add(nextStepB);
                        return true;
                    }
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void sendAllMessage(String msg) {
        if (null != WebSocketServer.users.get(playerA.getId())) {
            WebSocketServer.users.get(playerA.getId()).sendMessage(msg);
        }
        if (null != WebSocketServer.users.get(playerB.getId())) {
            WebSocketServer.users.get(playerB.getId()).sendMessage(msg);
        }
    }

    private void sendMove() {
        JSONObject resp = new JSONObject();
        resp.put("event", "move");
        lock.lock();
        try {
            resp.put("a_direction", nextStepA);
            resp.put("b_direction", nextStepB);
            nextStepA = nextStepB = null;
            sendAllMessage(resp.toJSONString());
        } finally {
            lock.unlock();
        }
    }

    private void sendResult() {
        JSONObject resp = new JSONObject();
        resp.put("event", "result");
        resp.put("loser", loser);
        sendAllMessage(resp.toJSONString());
        saveToDatabase();
    }

    private void updateUserRating(Player player, Integer delta) {
        User user = WebSocketServer.userMapper.selectById(player.getId());
        user.setRating(user.getRating() + delta);
        WebSocketServer.userMapper.updateById(user);
    }

    private void saveToDatabase() {
        if (!"all".equals(loser)) {
            if ("A".equals(loser)) {
                updateUserRating(playerA, -2);
                updateUserRating(playerB, 5);
            } else if ("B".equals(loser)) {
                updateUserRating(playerA, 5);
                updateUserRating(playerB, -2);
            }
        }

        Record record = new Record(
                null,
                playerA.getId(),
                playerA.getSx(),
                playerA.getSy(),
                playerB.getId(),
                playerB.getSx(),
                playerB.getSy(),
                playerA.getStepsString(),
                playerB.getStepsString(),
                getMapString(),
                loser,
                new Date()
        );
        WebSocketServer.recordMapper.insert(record);
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i ++ ) {
            if (nextStep()) {
                judge();
                if ("playing".equals(status)) {
                    sendMove();
                } else if ("finished".equals(status)) {
                    sendResult();
                    break;
                }
            } else {
                status = "finished";
                lock.lock();
                try {
                    if (null == nextStepA && null == nextStepB) {
                        loser = "all";
                    } else if (null == nextStepA) {
                        loser = "A";
                    } else if (null == nextStepB) {
                        loser = "B";
                    }
                } finally {
                    lock.unlock();
                }
                sendResult();
                break;
            }
        }
    }
}
