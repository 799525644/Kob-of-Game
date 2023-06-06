package com.jue.botrunningsystem.service.utils;

import com.jue.botrunningsystem.utils.BotInterface;
import org.joor.Reflect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * @className: com.jue.botrunningsystem.service.utils.Consumer.java
 * @description:
 * @author: Juemuel
 * @createTime: 2023/5/12
 **/
@Component
public class Consumer extends Thread{
    private Bot bot;
    private static RestTemplate restTemplate;
    private final static String receiveBotMoveUrl = "http://127.0.0.1:3001/pk/receive/bot/move/";

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        Consumer.restTemplate = restTemplate;
    }

    public void startTimeout(long timeout, Bot bot){
        this.bot = bot;
        this.start();
        // join阻塞timout秒，一旦时间到就就结束线程（和sleep不同，sleep必须等待timout秒，join是最多timout秒）
        try {
            this.join(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            this.interrupt();
        }
    }

    // 在code中的Bot类名后增加uid
    private String addUid(String code, String uid){
        // 匹配implements com.jue.botrunningsystem.utils.BotInterface后，
        int k = code.indexOf(" implements com.jue.botrunningsystem.utils.BotInterface");
        return code.substring(0,k) + uid + code.substring(k);
    }

    @Override
    public void run(){
        // UUID是一个API，返回随机字符串
        UUID uuid = UUID.randomUUID();
        String uid = uuid.toString().substring(0,8);

        // Reflect是joor 用来动态编译java（没有用docker因此需要用uid保证不同）
        BotInterface botInterface = Reflect.compile(
                "com.jue.botrunningsystem.utils.Bot" + uid,
                addUid(bot.getBotCode(),uid)
        ).create().get();

        Integer direction = botInterface.nextMove(bot.getInput());
        System.out.println("ok:move-direction: id-"+bot.getUserId() + " dir-" + direction);

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("user_id",bot.getUserId().toString());
        data.add("direction",direction.toString());

        restTemplate.postForObject(receiveBotMoveUrl,data,String.class);
    }

}
