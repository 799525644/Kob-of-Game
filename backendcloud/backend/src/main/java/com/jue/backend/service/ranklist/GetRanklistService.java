package com.jue.backend.service.ranklist;

import com.alibaba.fastjson.JSONObject;

/**
 * @className: com.jue.backend.service.ranklist.GetRanklistService.java
 * @description:
 * @author: Juemuel
 * @createTime: 2023/6/7
 **/
public interface GetRanklistService {
    JSONObject getList(Integer page);
}
