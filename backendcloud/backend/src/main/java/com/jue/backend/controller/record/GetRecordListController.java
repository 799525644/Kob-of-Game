package com.jue.backend.controller.record;

import com.alibaba.fastjson.JSONObject;
import com.jue.backend.service.record.GetRecordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @className: com.jue.backend.controller.record.GetRecordListController.java
 * @description:
 * @author: Juemuel
 * @createTime: 2023/6/6
 **/
@RestController
public class GetRecordListController {
    @Autowired
    private GetRecordListService getRecordListService;

    @GetMapping("/record/getlist/")
    JSONObject getList(@RequestParam Map<String,String> data){
        System.out.println("controller URL: /record/getlist/ ");
        Integer page = Integer.parseInt(data.get("page"));
        return getRecordListService.getList(page);
    }
}
