package com.jue.matchingsystem.service.impl.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @className: com.jue.matchingsystem.service.impl.utils.MatchingPool.java
 * @description:
 * 为了将spring的bean注入进来，需要加@component
 * @author: Juemuel
 * @createTime: 2023/4/17
 **/
@Component
public class MatchingPool extends Thread{
    private static List<Player> players = new ArrayList<>(); // 该变量有读写冲突的问题，故而使用加锁
    private final ReentrantLock lock = new ReentrantLock();

    private static RestTemplate restTemplate;
    private final static String startGameUrl = "http://127.0.0.1:3001/pk/start/game/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate){
        MatchingPool.restTemplate = restTemplate;
    }

    public void addPlayer(Integer userId, Integer rating){
        lock.lock();
        try{
            players.add(new Player(userId, rating, 0));
        }finally {
            lock.unlock();
        }
    }

    public void removePlayer(Integer userId){
        lock.lock();
        try{
            List<Player> newPlayers = new ArrayList<>(); // 创建一个新的列表，将没有删的存在下来
            for(Player player:players){
                if(!player.getUserId().equals(userId)){
                    newPlayers.add(player);
                }
            }
            players = newPlayers; // 赋值即可

        }finally {
            lock.unlock();
        }
    }

    private  void increaseWaitngTime(){
        for(Player player:players){
            player.setWaitingTime(player.getWaitingTime() + 1);
        }
    }

    /**
     * Method: 匹配判断 checkMatched
     * @Description: 判断两个Player是否能够匹配
     * 此处策略为：通过权值分差和a、b等待时间，a和b都能接受的时候匹配
     * @Author: Juemuel
     * @CreateTime: 2023/4/18
     * @Param: [a, b]
     * @Return: boolean
     */
    private boolean checkMatched(Player a, Player b){
        int ratingDelta = Math.abs(a.getRating() - b.getRating());
        int waitingTime = Math.min(a.getWaitingTime(), b.getWaitingTime());
        return ratingDelta <= waitingTime * 10;
    }
    /**
     * Method: 匹配结果返回 sendResult
     * @Description: sendResult
     * @Author: Juemuel
     * @CreateTime: 2023/4/18
     * @Param: [a, b]
     * @Return: void
     */
    private void sendResult(Player a, Player b){
        System.out.println("send result：" + a + " " + b);
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("a_id",a.getUserId().toString());
        data.add("b_id",b.getUserId().toString());
        restTemplate.postForObject(startGameUrl, data, String.class);
    }
    /**
     * Method: 匹配玩家 matchPlayers
     * @Description: matchPlayers
     * @Author: Juemuel
     * @CreateTime: 2023/4/18
     * @Param: []
     * @Return: void
     */
    protected void matchPlayers() {
        // 可以写一个防抖函数
        System.out.println("match players：" + players.toString()); // 由于线程每s执行一次匹配
        //TODO 存储匹配成功的两名玩家（可以优化吧）
        boolean[] used = new boolean[players.size()];
        for (int i = 0; i < players.size(); i++) {
            if(used[i]) continue;
            for (int j = i + 1; j < players.size() ; j++) {
                if(used[j]) continue;
                Player a = players.get(i) , b = players.get(j);
                if (checkMatched(a,b)){
                    used[i] = used[j] = true;
                    sendResult(a,b);
                    break;
                }
            }
        }
        List<Player> newPlayers = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            if(!used[i]){
                newPlayers.add(players.get(i));
            }
        }
        players = newPlayers;
    }

    @Override
    public void run() {
        while(true){
            try{// 每隔1s+1，尝试匹配玩家
                Thread.sleep(1000);
                lock.lock();
                try{
                    increaseWaitngTime();
                    matchPlayers();
                }finally {
                    lock.unlock();
                }
            }catch (InterruptedException e){
                e.printStackTrace();
                break;
            }
        }
    }
}
