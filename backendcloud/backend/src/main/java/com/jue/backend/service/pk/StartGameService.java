package com.jue.backend.service.pk;

/**
 * @className: com.jue.backend.service.pk.StartGameService.java
 * @description:
 * @author: Juemuel
 * @createTime: 2023/4/18
 **/
public interface StartGameService {
    String startGame(Integer aId, Integer aBotId, Integer bId, Integer bBotId);
}
