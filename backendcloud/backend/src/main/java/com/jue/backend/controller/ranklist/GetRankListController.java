package com.jue.backend.controller.ranklist;

import com.alibaba.fastjson.JSONObject;
import com.jue.backend.service.ranklist.GetRanklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @className: com.jue.backend.controller.ranklist.GetRankListController.java
 * @description:
 * @author: Juemuel
 * @createTime: 2023/6/7
 **/
@RestController
public class GetRankListController {
    @Autowired
    private GetRanklistService getRanklistService;

    @GetMapping(("/api/ranklist/getlist/"))
    public JSONObject getList(@RequestParam Map<String, String> data) {
        System.out.println("controller URL: /ranklist/getlist/ ");
        Integer page = Integer.parseInt(data.get("page"));
        return getRanklistService.getList(page);
    }
}
