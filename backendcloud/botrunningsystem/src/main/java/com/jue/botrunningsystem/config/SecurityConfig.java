package com.jue.botrunningsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * @className: com.jue.botrunningsystem.config.SecurityConfig.java
 * @description: 提供放行访问
 * @author: Juemuel
 * @createTime: 2023/4/17
 **/

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/bot/add/").hasIpAddress("127.0.0.1") // 只允许本地访问
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .anyRequest().authenticated();
    }

}
