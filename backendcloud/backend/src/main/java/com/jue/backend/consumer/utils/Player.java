package com.jue.backend.consumer.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor // 表示有參构造函数
@NoArgsConstructor // 表示无參构造函数
public class Player {
    private Integer id; // id
    private Integer botId; // -1表示亲自出马，否则表示用AI打
    private String botCode;
    private Integer sx; // 横坐标
    private Integer sy; // 纵坐标
    private List<Integer> steps;

    private boolean check_tail_increasing(int step) {  // 机制：检验当前回合，蛇的长度是否还会增加（10内长1，3步长1）
        if (step <= 10) return true;
        return step % 3 == 1;
    }

    public List<Cell> getCells() {
        List<Cell> res = new ArrayList<>();

        // -1010，010-1，记住偏移量
        int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};
        int x = sx, y = sy;
        int step = 0;
        res.add(new Cell(x, y));// 加入起点
        for (int d: steps) {
            x += dx[d];
            y += dy[d];
            res.add(new Cell(x, y));
            if (!check_tail_increasing( ++ step)) { // 蛇尾增加的则删尾
                res.remove(0);
            }
        }
        return res;
    }

    public String getStepsString() {
        StringBuilder res = new StringBuilder();
        for (int d: steps) {
            res.append(d);
        }
        return res.toString();
    }
}
