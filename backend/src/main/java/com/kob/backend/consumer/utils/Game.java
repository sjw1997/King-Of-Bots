package com.kob.backend.consumer.utils;

import java.util.Arrays;
import java.util.Random;

public class Game {
    private final int rows;
    private final int cols;
    private final int innerWallsCount;
    private final boolean[][] gamemap;
    private final int[] dx = {-1, 0, 1, 0};
    private final int[] dy = {0, 1, 0, -1};

    public Game(int rows, int cols, int innerWallsCount) {
        this.rows = rows;
        this.cols = cols;
        this.innerWallsCount = innerWallsCount;
        this.gamemap = new boolean[rows][cols];
    }

    public boolean[][] getGamemap() {
        return gamemap;
    }

    public void createMap() {
        for (int i = 0; i < 1000; i ++ ) {
            if (createWalls()) {
                break;
            }
        }
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
}
