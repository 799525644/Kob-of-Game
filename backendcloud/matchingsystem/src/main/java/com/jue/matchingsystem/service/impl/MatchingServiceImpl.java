package com.jue.matchingsystem.service.impl;

import com.jue.matchingsystem.service.MatchingService;
import com.jue.matchingsystem.service.impl.utils.MatchingPool;
import org.springframework.stereotype.Service;

/**
 * @className: backendcloud.com.jue.matchingsystem.service.impl.MatchingServiceImpl.java
 * @description:
 * @author: Juemuel
 * @createTime: 2023/4/16
 **/
@Service
public class MatchingServiceImpl implements MatchingService {

    public final static MatchingPool matchingPool = new MatchingPool();

    @Override
    public String addPlayer(Integer userId, Integer rating) {
        System.out.println("add player:"+ userId + " " + rating);
        matchingPool.addPlayer(userId,rating);
        return "add player success";
    }

    @Override
    public String removePlayer(Integer userId) {
        System.out.println("remove player:"+ userId );
        matchingPool.removePlayer(userId);
        return "remove player success";
    }
}
