package com.jue.matchingsystem.service;


/**
 * @author jvjielv
 * @version 1.0.0
 * @ClassName backendcloud.com.jue.matchingsystem.service.MatchingService.java
 * @Description TODO
 * @createTime 2023年04月16日
 */
public interface MatchingService {
    String addPlayer(Integer userId, Integer rating);
    String removePlayer(Integer userId);
}
