package com.jue.backend.controller.user;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jue.backend.mapper.UserMapper;
import com.jue.backend.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.print.DocFlavor;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserMapper userMapper; // 加上Autowired；对应数据库

    // RequstMapping映射全部请求，GetMapping只映射get
    // 1. 路径映射
    @GetMapping("/user/all")
    public List<User> getAll(){
        return userMapper.selectList(null);
    }


    // 返回一个时用对象名，返回多个时用List<对象名>
    // 2. 含参路径映射
    @GetMapping("/user/{userId}")
    public List<User> getuser(@PathVariable int userId){

//        1. 查找1个的两种方式
        // #1 直接方法查找
//        userMapper.selectById(userId);
        // #2 用QueryWrpper查找 具体见 Mybatis plus 的 API
//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("id",userId);// 查找id等于userId的值
//        userMapper.selectOne(queryWrapper); // 用查询条件查找一个

//        2.查找一组的方式
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("id", 2).le("id",3); // 查找  ID < x 是g； x < ID 是 l；需要相等后面加e
        userMapper.selectList(queryWrapper); // 用查询条件查找一组
        return userMapper.selectList(queryWrapper);
    }

    @GetMapping("/user/add/{userId}/{username}/{password}")
    public String addUser(@PathVariable int userId, @PathVariable String username, @PathVariable String password){
        if(password.length()<6){
            return "密码过短"
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(userId, username, encodedPassword);
        userMapper.insert(user);
        return "Add User Successfully";
    }

    @GetMapping("/user/delete/{userId}")
    public String deleteUser(@PathVariable int userId){
        userMapper.deleteById(userId);
        return "Delete User Successfully";
    }

}
