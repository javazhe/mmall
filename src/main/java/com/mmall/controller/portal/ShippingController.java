package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ResponseService;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by jia on 2018/6/20.
 */
@Controller
@RequestMapping("/shipping/")
public class ShippingController {
    @Autowired
    private IShippingService iShippingService;


    @RequestMapping(value="add.do" ,method= RequestMethod.POST)
    @ResponseBody
    public ResponseService add(HttpSession session ,Shipping shipping){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }else{
            return iShippingService.add(shipping,currentUser.getId());
        }
    }
    @RequestMapping(value="del.do" ,method= RequestMethod.POST)
    @ResponseBody
    public ResponseService del(HttpSession session ,Integer shippingId){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }else{
            return iShippingService.del(shippingId,currentUser.getId());
        }
    }

    @RequestMapping(value="update.do" ,method = RequestMethod.POST)
    @ResponseBody
    public ResponseService update(HttpSession session,Shipping shipping){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }else{
            return iShippingService.update(currentUser.getId(),shipping);
        }
    }

    @RequestMapping(value= "select.do", method = RequestMethod.POST)
    @ResponseBody
    public ResponseService select(HttpSession session,Integer shippingId){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }else{
            return iShippingService.select(currentUser.getId(),shippingId);
        }
    }


    @RequestMapping(value= "list.do", method = RequestMethod.POST)
    @ResponseBody
    public ResponseService select(HttpSession session, @RequestParam(value="pageNum",defaultValue = "1") Integer pageNum,@RequestParam(value ="pageSize",defaultValue = "10") Integer pageSize){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }else{
            return iShippingService.list(currentUser.getId(),pageNum,pageSize);
        }
    }
}
