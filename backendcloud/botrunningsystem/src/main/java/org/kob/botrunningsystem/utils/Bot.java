package org.kob.botrunningsystem.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bot implements org.kob.botrunningsystem.utils.BotInterface{
    static class Cell {
        int x, y;
        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // 检验当前回合蛇的长度是否会增加
    private boolean checkTailIncreasing(int steps) {
        if (steps < 10) {
            return true;
        }
        return steps % 3 == 1;
    }

    public List<Cell> getCells(int sx, int sy, String steps) {
        steps = steps.substring(1, steps.length() - 1);
        List<Cell> res = new ArrayList<>();

        int[] dx = {-1, 0 ,1, 0}, dy = {0, 1, 0, -1};

        int x = sx, y = sy;
        res.add(new Cell(x, y));
        int step = 0;
        for (int i = 0; i < steps.length(); i ++ ) {
            int d = steps.charAt(i) - '0';
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            if (!checkTailIncreasing(++ step)) {
                res.remove(0);
            }
        }

        return res;
    }

    @Override
    public Integer nextMove(String input) {
        String[] strs = input.split("#");
        int rows = 13, cols = 14;
        int[][] g = new int[rows][cols];
        for (int i = 0, k = 0; i < rows; i ++ ) {
            for (int j = 0; j < cols; j ++ , k ++ ) {
                if (strs[0].charAt(k) == '1') {
                    g[i][j] = 1;
                }
            }
        }

        int aSx = Integer.parseInt(strs[1]), aSy = Integer.parseInt(strs[2]);
        int bSx = Integer.parseInt(strs[4]), bSy = Integer.parseInt(strs[5]);


        List<Cell> aCells = getCells(aSx, aSy, strs[3]);
        List<Cell> bCells = getCells(bSx, bSy, strs[6]);

        for (Cell c : aCells) g[c.x][c.y] = 1;
        for (Cell c : bCells) g[c.x][c.y] = 1;

        int[] dx = {-1, 0 ,1, 0}, dy = {0, 1, 0, -1};
        List<Integer> validDirections = new ArrayList<>();
        for (int i = 0; i < 4; i ++ ) {
            int x = aCells.get(aCells.size() - 1).x + dx[i];
            int y = aCells.get(aCells.size() - 1).y + dy[i];
            if (x >= 0 && x < rows && y >= 0 && y < cols && 0 == g[x][y]) {
                validDirections.add(i);
            }
        }

        if (validDirections.isEmpty()) {
            return 0;
        }

        Random random = new Random();
        return validDirections.get(random.nextInt(validDirections.size()));
    }
}
