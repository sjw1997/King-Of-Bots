package com.kob.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private Integer id;
    private Integer botId;
    private String botCode;
    private Integer sx;
    private Integer sy;
    private List<Integer> steps;

    private boolean checkTailIncreasing(int step) {
        if (step <= 10) {
            return true;
        }
        return 1 == step % 3;
    }

    public List<Cell> getCells() {
        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};

        List<Cell> res = new ArrayList<>();
        int step = 0;
        int x = sx, y = sy;
        res.add(new Cell(x, y));
        for (int d : steps) {
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            step ++ ;
            if (!checkTailIncreasing(step)) {
                res.remove(0);
            }
        }

        return  res;
    }

    public String getStepsString() {
        StringBuilder res = new StringBuilder();
        for (int d : steps) {
            res.append(d);
        }
        return res.toString();
    }
}
