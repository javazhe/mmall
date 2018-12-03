package com.mmall.service;

import com.mmall.common.ResponseService;

import java.util.Map;

/**
 * Created by jia on 2018/11/15.
 */

public interface IOrderService {
    ResponseService pay(Integer userId, Long orderNo, String path);

    ResponseService alipayCallback(Map<String ,String> params);

    ResponseService queryByUserIdOrderNo(Integer userId,Long orderNo);
}
