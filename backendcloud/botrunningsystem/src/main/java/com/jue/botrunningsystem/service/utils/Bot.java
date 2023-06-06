package com.jue.botrunningsystem.service.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className: com.jue.botrunningsystem.service.utils.Bot.java
 * @description:
 * @author: Juemuel
 * @createTime: 2023/5/12
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bot {
    Integer userId;
    String botCode;
    String input;
}
