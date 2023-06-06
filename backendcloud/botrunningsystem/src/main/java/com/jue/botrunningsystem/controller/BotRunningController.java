package com.jue.botrunningsystem.controller;

import com.jue.botrunningsystem.service.BotRunningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @className: com.jue.botrunningsystem.controller.BotRunningController.java
 * @description:
 * @author: Juemuel
 * @createTime: 2023/4/28
 **/
@RestController
public class BotRunningController {
    @Autowired
    private BotRunningService botRunningService;

//    到底要不要加/，主要是看前后端要一致
    @PostMapping("/bot/add/")
    public String addBot(@RequestParam MultiValueMap<String, String> data){
        Integer userId = Integer.parseInt(Objects.requireNonNull(data.getFirst("user_id")));
        String botCode = data.getFirst("bot_code");
        String input = data.getFirst("input");
        return botRunningService.addBot(userId, botCode, input);
    }
}
