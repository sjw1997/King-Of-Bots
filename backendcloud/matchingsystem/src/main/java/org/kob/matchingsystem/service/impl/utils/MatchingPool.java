package org.kob.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class MatchingPool extends Thread {
    private static List<Player> players = new ArrayList<>();
    private final ReentrantLock lock = new ReentrantLock();
    private static RestTemplate restTemplate;
    private final static String startGameUrl = "http://127.0.0.1:3000/pk/start/game/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        MatchingPool.restTemplate = restTemplate;
    }

    public void addPlayer(Integer userId, Integer rating) {
        lock.lock();
        boolean hasInPool = false;
        try {
            for (Player player : players) {
                if (player.getId().equals(userId)) {
                    hasInPool = true;
                    break;
                }
            }
            if (!hasInPool) {
                MatchingPool.players.add(new Player(userId, rating, 0));
            }
        } finally {
            lock.unlock();
            if (hasInPool) {
                removePlayer(userId);
                addPlayer(userId, rating);
            }
        }
    }

    public void removePlayer(Integer userId) {
        lock.lock();
        try {
            int index = -1;
            for (int i = 0; i < players.size(); i ++ ) {
                if (players.get(i).getId().equals(userId)) {
                    index = i;
                    break;
                }
            }

            if (index >= 0) {
                players.remove(index);
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean checkValid(Player a, Player b) {
        int ratingDelta = Math.abs(a.getRating() - b.getRating());
        int waitingTimeMin = Math.min(a.getWaitingTime(), b.getWaitingTime());
        return ratingDelta <= waitingTimeMin * 10;
    }

    private void sendMatchResult(Integer aId, Integer bId) {
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("a_id", aId.toString());
        data.add("b_id", bId.toString());
        MatchingPool.restTemplate.postForObject(startGameUrl, data, String.class);
    }

    private void matching() {
        lock.lock();
        try {
            boolean[] used = new boolean[players.size()];
            for (int i = 0; i < players.size(); i ++ ) {
                if (!used[i]) {
                    for (int j = i + 1; j < players.size(); j ++ ) {
                        if (!used[j]) {
                            Player a = players.get(i), b = players.get(j);
                            if (checkValid(a, b)) {
                                used[i] = used[j] = true;
                                sendMatchResult(a.getId(), b.getId());
                                break;
                            }
                        }
                    }
                }
            }

            List<Player> newPlayers = new ArrayList<>();
            for (int i = 0; i < players.size(); i ++ ) {
                if (!used[i]) {
                    newPlayers.add(players.get(i));
                }
            }
            players = newPlayers;
        } finally {
            lock.unlock();
        }
    }

    private void increaseWaitingTime() {
        lock.lock();
        try {
            for (Player player : players) {
                player.setWaitingTime(player.getWaitingTime() + 1);
            }
            System.out.println(MatchingPool.players.toString());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
                increaseWaitingTime();
                matching();
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
