package com.jue.backend.consumer.utils;

import com.alibaba.fastjson.JSONObject;
import com.jue.backend.consumer.WebSocketServer;
import com.jue.backend.pojo.Record;
import org.apache.catalina.core.AprLifecycleListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Game extends Thread{
        //Game维护属性
        private final  Integer rows;// 行
        private final  Integer cols;// 列
        private final Integer inner_walls_count;// 障碍物墙体数
        private final int[][] g; // 地图
        private final Player playerA, playerB; // 玩家
        //辅助属性
        private final static int[] dx = {-1, 0, 1, 0}, dy = {0, 1, 0, -1};// 偏移量
        private Integer nextStepA = null; // 玩家的下一步操作，异步操作，读写均需要上锁
        private Integer nextStepB = null; // 玩家下一步操作，异步操作，读写均需要上锁
        private ReentrantLock lock = new ReentrantLock(); // 定义一个锁，在需要的地方用来加锁
        private String status = "playing";
        private String loser = "";  // all：平局，A：A输，B：B输
        /**
         * Method: Game构造函数
         * @Description: Game构造函数
         * @Author: Juemuel
         * @CreateTime: 2023/4/15
         * @Param: [rows, cols, inner_walls_count, idA, idB]
         * @Return:
         */
        public Game(Integer rows, Integer cols, Integer inner_walls_count, Integer idA, Integer idB) {
            this.rows = rows;
            this.cols = cols;
            this.inner_walls_count = inner_walls_count;
            this.g = new int[rows][cols];
            playerA = new Player(idA, rows - 2, 1, new ArrayList<>());
            playerB = new Player(idB, 1, cols-2, new ArrayList<>());
        }
        public Player getPlayerA(){
            return playerA;
        }
        public Player getPlayerB(){
            return playerB;
        }
        public void setNextStepA(Integer nextStepA){
            lock.lock();
            try{
                this.nextStepA = nextStepA;
            }finally {
                lock.unlock(); // 不管有没有异常都解锁
            }
        }
        public void setNextStepB(Integer nextStepB){
            lock.lock();
            try{
                this.nextStepB = nextStepB;
            }finally {
                lock.unlock(); // 不管有没有异常都解锁
            }

        }
        public int[][] getG() {
            return g;
        }

        // 1. 地图生成
        /**
         * Method: 连通性检查 check_connectivity
         * @Description: check_connectivity
         * @Author: Juemuel
         * @CreateTime: 2023/4/16
         * @Param: [sx, sy, tx, ty]
         * @Return: boolean
         */
        private boolean check_connectivity(int sx, int sy, int tx, int ty) {
            if (sx == tx && sy == ty) return true;
            g[sx][sy] = 1;

            for (int i = 0; i < 4; i ++ ) {
                int x = sx + dx[i], y = sy + dy[i];
                if (x >= 0 && x < this.rows && y >= 0 && y < this.cols && g[x][y] == 0) {
                    if (check_connectivity(x, y, tx, ty)) {
                        g[sx][sy] = 0;
                        return true;
                    }
                }
            }

            g[sx][sy] = 0;
            return false;
        }
        /**
         * Method: 生成地图 draw
         * @Description: draw
         * @Author: Juemuel
         * @CreateTime: 2023/4/16
         * @Param: []
         * @Return: boolean
         */
        private boolean draw() {
            // 初始化
            for (int i = 0; i < this.rows; i ++ ) {
                for (int j = 0; j < this.cols; j ++ ) {
                    g[i][j] = 0;
                }
            }

            // 先四周
            for (int r = 0; r < this.rows; r ++ ) {
                g[r][0] = g[r][this.cols - 1] = 1;
            }
            for (int c = 0; c < this.cols; c ++ ) {
                g[0][c] = g[this.rows - 1][c] = 1;
            }
            // 外面一层：生成墙的个数一定小于总墙数的一半，里面一层：如果随机生成对称墙就跳出
            Random random = new Random();
            for (int i = 0; i < this.inner_walls_count / 2; i ++ ) {
                for (int j = 0; j < 1000; j ++ ) {
                    int r = random.nextInt(this.rows);
                    int c = random.nextInt(this.cols);

                    if (g[r][c] == 1 || g[this.rows - 1 - r][this.cols - 1 - c] == 1)
                        continue;
                    if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2)
                        continue;

                    g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = 1;
                    break;
                }
            }

            return check_connectivity(this.rows - 2, 1, 1, this.cols - 2);
        }
        /**
         * Method: 创建地图 createMap
         * @Description: createMap 略
         * @Author: Juemuel
         * @CreateTime: 2023/4/16
         * @Param: []
         * @Return: void
         */
        public void createMap() {
            for (int i = 0; i < 1000; i ++ ) {
                if (draw())
                    break;
            }
        }


        // 2. 等待两个玩家的下一步操作
        private boolean nextStep(){
            System.out.println("nextStep");
            // 如果某名玩家输入快，1s输入多次（比如bot），而后端实际上是收两边作为一步操作
            // 那么前端输入快的玩家将会有有很多步被覆盖掉，需要先sleep最小值（频次）
            try{// 同步前后端执行下一步的速度（机机可能输入很快）
                Thread.sleep(200); // 前端设置速度为5，即每200ms可以走一步，那么做一个限制，每200ms内接受1次操作
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //判断5s，每一步sleep 1s做一次开锁解锁操作，然后锁上的过程中，判断两名玩家输入
            for(int i = 0 ; i < 50; i++){
                try{
                    // 允许前端输入的等待时间，先sleep 1000ms 5次延迟体验不佳，改成100ms 50次
                    Thread.sleep(100); // 延迟在200ms内都感觉不出的，再锁（不过服务端压力大一些）
                    lock.lock(); // 拿到锁了
                    try{
                        if(nextStepA != null && nextStepB != null){// 都不是空的true
                            playerA.getSteps().add(nextStepA);
                            playerB.getSteps().add(nextStepB);
                            return true;
                        }
                    }finally {
                        lock.unlock(); // 解锁
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();// 输出即可
                }
            }
            return false;
        }

        // 判断两名玩家操作是否合法
        private boolean check_valid(List<Cell> cellsA, List<Cell> cellsB) {
            System.out.println("check_valid");
            int n = cellsA.size();
            Cell cell = cellsA.get(n - 1);
            if (g[cell.x][cell.y] == 1) return false;

            for (int i = 0; i < n - 1; i ++ ) {
                if (cellsA.get(i).x == cell.x && cellsA.get(i).y == cell.y)
                    return false;
            }

            for (int i = 0; i < n - 1; i ++ ) {
                if (cellsB.get(i).x == cell.x && cellsB.get(i).y == cell.y)
                    return false;
            }

            return true;
        }
        private void judge(){
            System.out.println("judge");
            List<Cell> cellsA = playerA.getCells();
            List<Cell> cellsB = playerB.getCells();

            boolean validA = check_valid(cellsA, cellsB);
            boolean validB = check_valid(cellsB, cellsA);
            if (!validA || !validB) {
                System.out.println("A valid:"+validA + " B valid:"+validB);
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
        // sendMove封装移动信息 -> sendAllMessage发送
        private void sendMove(){// 向两个client传递移动信息，并清空
            System.out.println("sendMove");
            lock.lock();
            try{
                JSONObject resp = new JSONObject();
                resp.put("event","move");
                resp.put("a_direction",nextStepA);
                resp.put("b_direction",nextStepB);
                System.out.println(resp.toJSONString());
                sendAllMessage(resp.toJSONString());
                nextStepA = nextStepB = null;
            }finally {
                lock.unlock();
            }
        }
        // sendResult封装结果信息->sendAllMessage发送
        private void sendResult(){ //
            JSONObject resp = new JSONObject();
            resp.put("event","result");
            resp.put("loser",loser);
            saveToDatabase();
            sendAllMessage(resp.toJSONString());
        }
        // 发送到Client端
        private void sendAllMessage(String message){
            if(WebSocketServer.users.get(playerA.getId())!=null)
                WebSocketServer.users.get(playerA.getId()).sendMessage(message);
            if(WebSocketServer.users.get(playerB.getId())!=null)
                WebSocketServer.users.get(playerB.getId()).sendMessage(message);
        }
        // map转string
        private String getMapString() {
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < rows; i ++ ) {
                for (int j = 0; j < cols; j ++ ) {
                    res.append(g[i][j]);
                }
            }
            return res.toString();
        }
        private void saveToDatabase() {
            Record record = new Record(
                    null, // id自增写null
                    playerA.getId(),
                    playerA.getSx(),
                    playerA.getSy(),
                    playerB.getId(),
                    playerB.getSx(),
                    playerB.getSy(),
                    playerA.getStepsString(), // 要把steps转String哦~所以去Player里写一个
                    playerB.getStepsString(), // 同上
                    getMapString(),// 要把map转String哦~所以在上面写一个
                    loser,
                    new Date()
            );

            WebSocketServer.recordMapper.insert(record);
        }
        // Thread的run执行
        @Override
        public void run() {
            for (int i = 0; i < 1000; i++) { // 循环1000步肯定能结束了
                if(nextStep()){ // 每一次循环判断一次两边的操作
                    judge();
                    if(status.equals("playing")){
                        sendMove();
                    }else{
                        sendResult();
                        break;
                    }
                }else{
                    status = "finished";
                    lock.lock(); // 下面也是nextStep的读操作，要加锁
                    try {
                        if(nextStepA == null && nextStepB == null){
                            loser = "all";
                        }else if(nextStepA == null){
                            loser = "A";
                        }else if(nextStepB == null){
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
