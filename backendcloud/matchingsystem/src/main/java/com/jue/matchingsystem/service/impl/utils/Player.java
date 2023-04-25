package com.jue.matchingsystem.service.impl.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className: com.jue.matchingsystem.service.impl.utils.Player.java
 * @description:
 * @author: Juemuel
 * @createTime: 2023/4/17
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private Integer userId;
    private Integer rating;
    private Integer waitingTime;

}
