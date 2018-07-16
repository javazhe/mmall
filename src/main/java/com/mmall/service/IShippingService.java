package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ResponseService;
import com.mmall.pojo.Shipping;

/**
 * Created by jia on 2018/6/20.
 */
public interface IShippingService {
    ResponseService add(Shipping shipping, Integer userId);

    ResponseService del(Integer shippingId, Integer userId);

    ResponseService update(Integer userId,Shipping shipping);

    ResponseService<Shipping> select(Integer userId, Integer shippingId);

    ResponseService<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize);
}
