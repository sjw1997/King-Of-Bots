package org.kob.botrunningsystem.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class SmarterBot implements java.util.function.Supplier<Integer>{
    static class Cell {
        int x, y;
        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    static final int[] dx = {-1, 0 ,1, 0}, dy = {0, 1, 0, -1};
    static final int MAXN = 30;
    static boolean st[][] = new boolean[MAXN][MAXN], bl[][] = new boolean[MAXN][MAXN];
    static int qx[] = new int[MAXN * MAXN];
    static int qy[] = new int[MAXN * MAXN];
    static int dist[][] = new int[MAXN][MAXN];
    int n, m;


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

    public Integer nextMove(String input) {
        String[] strs = input.split("#");
        int rows = 13, cols = 14;
        n = rows - 1;
        m = cols - 1;
        int[][] g = new int[MAXN][MAXN];
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

        // 本回合两条蛇是否生长
        if (!checkTailIncreasing(strs[3].length() - 2)) {
            aCells.remove(0);
            bCells.remove(0);
        }

        for (Cell c : aCells) g[c.x][c.y] = 1;
        for (Cell c : bCells) g[c.x][c.y] = 1;

//        List<Integer> validDirections = new ArrayList<>();
//        for (int i = 0; i < 4; i ++ ) {
//            int x = aCells.get(aCells.size() - 1).x + dx[i];
//            int y = aCells.get(aCells.size() - 1).y + dy[i];
//            if (x >= 0 && x < rows && y >= 0 && y < cols && 0 == g[x][y]) {
//                validDirections.add(i);
//            }
//        }
//
//        if (validDirections.isEmpty()) {
//            return 0;
//        }
//
//        Random random = new Random();
//        return validDirections.get(random.nextInt(validDirections.size()));

        return oneStepJudge(aCells, bCells, g);
    }

    private Cell getHead(List<Cell> snake) {
        return snake.get(snake.size() - 1);
    }

    private int oneStepJudge(List<Cell> snake0, List<Cell> snake1, int[][] g) {
        Cell p = getHead(snake0);
        int x = p.x, y = p.y;
        double best = -1e9, tmp;
        int ret = 0;
        for (int k = 0; k < 4; k ++ )  {
            if (0 == g[x + dx[k]][y + dy[k]]) {
                g[x + dx[k]][y + dy[k]] = 1;
                Cell p0 = new Cell(x + dx[k], y + dy[k]);
                tmp = calcDouble(g, p0, getHead(snake1));
                if (tmp > best) {
                    best = tmp;
                    ret = k;
                }
                g[x + dx[k]][y + dy[k]] = 0;
            }
        }
        return ret;
    }

    private double calcDouble(int[][] g, Cell p, Cell q) {
        for (boolean[] booleans : st) {
            Arrays.fill(booleans, false);
        }

        int head = 0, tail = 2, ux, uy;
        for (int[] ints : dist) {
            Arrays.fill(ints, 0);
        }

        qx[2] = p.x; qy[2] = p.y; dist[p.x][p.y] = 0; bl[p.x][p.y] = false;
        qx[1] = q.x; qy[1] = q.y; dist[q.x][q.y] = 0; bl[q.x][q.y] = true;
        while (head ++ < tail) {
            ux = qx[head]; uy = qy[head];
            for (int k = 0; k < 4; k ++ ) {
                if (0 == g[ux + dx[k]][uy + dy[k]] && !st[ux + dx[k]][uy + dy[k]]) {
                    st[ux + dx[k]][uy + dy[k]] = true;
                    dist[ux + dx[k]][uy + dy[k]] = dist[ux][uy] + 1;
                    bl[ux + dx[k]][uy + dy[k]] = bl[ux][uy];
                    tail ++ ;
                    qx[tail] = ux + dx[k]; qy[tail] = uy + dy[k];
                }
            }
        }

        double ret = 0;
        for (int i = 1; i <= n; i ++ ) {
            for (int j = 1; j <= m; j ++ ) {
                if (0 == g[i][j] && dist[i][j] != 0 && !bl[i][j]) {
                    ret += 1.0 / Math.sqrt(dist[i][j]);
                } else if (0 == g[i][j] && dist[i][j] != 0 && bl[i][j]) {
                    ret -= 1.0 / Math.sqrt(dist[i][j]);
                }
            }
        }
        return ret;
    }

    @Override
    public Integer get() {
        File file = new File("input.txt");
        try {
            Scanner sc = new Scanner(file);
            return nextMove(sc.next());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
