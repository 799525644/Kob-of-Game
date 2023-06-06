package com.jue.botrunningsystem.service.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @className:
 * @description:
 * @author: Juemuel
 * @createTime: 2023/5/12
 **/
public class BotPool extends Thread{

    private final static ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final Queue<Bot> bots = new LinkedList<>();

    public void addBot(Integer userId, String botCode, String input){
        lock.lock();
        try {
            bots.add(new Bot(userId, botCode,input));
            condition.signalAll();//当前、线程，signal和signalAll都没关系
            System.out.println("adding Bot to BotPool");
        } catch(Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
    // java代码，可以扩展安全操作、docker操作等
    private void consume(Bot bot){
        Consumer consumer = new Consumer();
        consumer.startTimeout(2000,bot);
    }


    @Override
    public void run(){
        while(true){
            lock.lock();
            if(bots.isEmpty()){
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    lock.unlock();
                    break;
                }
            }else{
                System.out.println("BotPool has bots:");
                Bot bot = bots.remove();// 移除并取出队头，即没有消息冲突了
                lock.unlock();// 此时即可解锁
                consume(bot);//比较耗时。可能执行几秒钟，放在unlock后面
            }
        }
    }
}
