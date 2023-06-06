package com.jue.botrunningsystem.service;

/**
 * @className: com.jue.botrunningsystem.service.BotRunningService.java
 * @description:
 * @author: Juemuel
 * @createTime: 2023/4/28
 **/
public interface BotRunningService {
    String addBot(Integer userId, String botCode, String input);
}
