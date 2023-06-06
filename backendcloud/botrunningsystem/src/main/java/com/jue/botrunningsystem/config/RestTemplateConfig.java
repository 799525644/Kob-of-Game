package com.jue.botrunningsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @className: com.jue.botrunningsystem.config.RestTemplateConfig.java
 * @description: 自定义注入内容？当需要用到某个...通过RestTemplate使之能够注入
 * @author: Juemuel
 * @createTime: 2023/4/17
 **/
@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
