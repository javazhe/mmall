package com.mmall.service;

import com.mmall.common.ResponseService;
import com.mmall.vo.CartVo;

/**
 * Created by jia on 2018/6/26.
 */
public interface ICartService {
    ResponseService<CartVo> add(Integer userId, Integer productId, Integer count);

    ResponseService<CartVo> update(Integer userId, Integer productId, Integer count);

    ResponseService<CartVo> list(Integer userId);

    ResponseService<CartVo> deleteProduct(Integer userId, String productIds);

    ResponseService<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked);

    ResponseService<Integer> getCartProductCount(Integer userId);
}
