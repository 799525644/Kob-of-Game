package com.jue.backend.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bot {
    // 数据库中是下划线，java中用驼峰，才能对应起来，Djiango也是。querryWrapper中用下划线
    // 自增、实践需要注解
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String title;
    private String description;
    private String content;
    private Integer rating;
    // 设置时区，前端和后端等时区默认不一定一致，（后端数据库中正确、前端显示不正确差8h）加上时区统一（东八区写上海）
    @JsonFormat(pattern = "yyy-MM0dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date createtime;
    @JsonFormat(pattern = "yyy-MM0dd HH:mm:ss",timezone = "Asia/Shanghai")
    private Date modifytime;
}
