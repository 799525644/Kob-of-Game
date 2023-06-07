package com.jue.backend.service.impl.record;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jue.backend.mapper.RecordMapper;
import com.jue.backend.mapper.UserMapper;
import com.jue.backend.pojo.Record;
import com.jue.backend.pojo.User;
import com.jue.backend.service.record.GetRecordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * @className: com.jue.backend.service.impl.record.GetRecordListServiceImpl.java
 * @description:
 * @author: Juemuel
 * @createTime: 2023/6/6
 **/
@Service
public class GetRecordListServiceImpl implements GetRecordListService {

    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private UserMapper userMapper;


    @Override
    public JSONObject getList(Integer page) {
        System.out.println("getList func:");
        // mybatis的一个API
        IPage<Record> recordIPage = new Page<>(page,10);
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");// 排序
        List<Record> records = recordMapper.selectPage(recordIPage,queryWrapper).getRecords();
        JSONObject resp = new JSONObject();
        List<JSONObject> items = new LinkedList<>();
        // 封装一下返回内容
        for(Record record:records){
            User userA = userMapper.selectById(record.getAId());
            User userB = userMapper.selectById(record.getBId());
            JSONObject item = new JSONObject();
            item.put("a_photo",userA.getPhoto());
            item.put("a_username",userA.getUsername());
            item.put("b_photo",userB.getPhoto());
            item.put("b_username",userB.getUsername());
            String result = "平局";
            if("A".equals(record.getLoser())) result = "B胜";
            else if("B".equals(record.getLoser())) result = "A胜";
            item.put("result",result);
            item.put("record",record);
            items.add(item);
        }

        resp.put("records",items);
        resp.put("records_count",recordMapper.selectCount(null));
        System.out.println("resp: " + resp);
        return resp;
    }
}
