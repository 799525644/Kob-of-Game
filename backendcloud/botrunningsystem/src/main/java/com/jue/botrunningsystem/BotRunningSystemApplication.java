package com.jue.botrunningsystem;

import com.jue.botrunningsystem.service.impl.BotRunningServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @className: com.jue.botrunningsystem.${NAME}.java
 * @description: ${description}
 * @author: Juemuel
 * @createTime: 2023/4/28
 **/
@SpringBootApplication
public class BotRunningSystemApplication {
    public static void main(String[] args) {
        BotRunningServiceImpl.botPool.start();
        SpringApplication.run(BotRunningSystemApplication.class, args);
    }
}