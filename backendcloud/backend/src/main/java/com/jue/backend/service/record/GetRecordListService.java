package com.jue.backend.service.record;

import com.alibaba.fastjson.JSONObject;

/**
 * @className: com.jue.backend.service.record.GetRecordListService.java
 * @description:
 * @author: Juemuel
 * @createTime: 2023/6/6
 **/
public interface GetRecordListService {

    JSONObject getList(Integer page);

}
