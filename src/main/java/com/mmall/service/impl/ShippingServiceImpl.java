package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.ResponseCode;
import com.mmall.common.ResponseService;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by jia on 2018/6/20.
 */
@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService{
    @Autowired
    private ShippingMapper shippingMapper;

    public ResponseService add(Shipping shipping,Integer userId){
        shipping.setUserId(userId);
        int resultCount  = shippingMapper.insert(shipping);
        if (resultCount > 0){
            Map result = Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return ResponseService.createBySuccess("添加地址成功",result);
        }else{
            return ResponseService.createByErrorMessage("添加地址失败");
        }
    }
    //这里要注意一下横向越权问题,要保证穿过来的shippingid属于当前用户,不能把别人的收获地址给删除了
    public ResponseService del(Integer shippingId, Integer userId){
        int result = shippingMapper.deleteShippingByUserIdAndSId(shippingId, userId);
        if (result > 0){
            return ResponseService.createBySuccessMessage("删除收货地址成功");
        }else{
            return ResponseService.createByErrorMessage("删除收货地址失败");
        }
    }

    public ResponseService update(Integer userId,Shipping shipping){
        shipping.setUserId(userId);
        int result = shippingMapper.updateByShipping(shipping);
        if (result > 0){
            return ResponseService.createBySuccessMessage("更新收货地址成功");
        }else{
            return ResponseService.createByErrorMessage("更新收货地址失败");
        }
    }

    public ResponseService<Shipping> select(Integer userId,Integer shippingId){
        Shipping shipping = shippingMapper.selectShippingIdAndUserId(userId, shippingId);
        if (shipping != null){
            return ResponseService.createBySuccess(shipping);
        }else{
            return ResponseService.createByCodeErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
    }

    @Override
    public ResponseService<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectListByUserId(userId);
        if (shippingList.size() > 0){
            PageInfo<Shipping> pageInfo = new PageInfo<>();
            pageInfo.setList(shippingList);
            return ResponseService.createBySuccess(pageInfo);
        }else{
            return ResponseService.createByErrorMessage("该用户未添加任何收货地址");
        }
    }
}
