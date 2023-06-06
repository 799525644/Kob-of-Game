package com.jue.backend.service.impl.pk;

import com.jue.backend.consumer.WebSocketServer;
import com.jue.backend.consumer.utils.Game;
import com.jue.backend.service.pk.ReceiveBotMoveService;
import org.springframework.stereotype.Service;

/**
 * @className: com.jue.backend.service.impl.pk.ReceiveBotMove.java
 * @description:
 * @author: Juemuel
 * @createTime: 2023/6/5
 **/
@Service
public class ReceiveBotMoveServiceImpl implements ReceiveBotMoveService {

    @Override
    public String receiveBotMove(Integer userId, Integer direction) {
        System.out.println("ready bot step:" + userId + " " + direction + " ");
        if(WebSocketServer.users.get(userId) != null){
            Game game = WebSocketServer.users.get(userId).game;
            if(game != null){
                System.out.println("check user to step！");
                if(game.getPlayerA().getId().equals(userId)){
                    System.out.println("this A");
                    game.setNextStepA(direction);
                }else if(game.getPlayerB().getId().equals(userId)){
                    System.out.println("this B");
                    game.setNextStepB(direction);
                }
            }
        }

        return "receive bot move success";
    }
}
