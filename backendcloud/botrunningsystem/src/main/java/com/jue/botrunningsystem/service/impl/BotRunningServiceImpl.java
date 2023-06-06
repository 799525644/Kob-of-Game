package com.jue.botrunningsystem.service.impl;

import com.jue.botrunningsystem.service.BotRunningService;
import com.jue.botrunningsystem.service.utils.BotPool;
import org.springframework.stereotype.Service;

/**
 * @className: com.jue.botrunningsystem.service.impl.BotRunningServiceImpl.java
 * @description:
 * @author: Juemuel
 * @createTime: 2023/4/28
 **/
@Service
public class BotRunningServiceImpl implements BotRunningService {
    public final static BotPool botPool = new BotPool();

    @Override
    public String addBot(Integer userId, String botCode, String input) {
        System.out.println("加载bot-"+userId + " success---------------");
        botPool.addBot(userId,botCode,input);
        return "add bot success";
    }
}
