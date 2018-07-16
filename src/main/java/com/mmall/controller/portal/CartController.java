package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ResponseService;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import com.mmall.service.impl.CartServiceImpl;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by jia on 2018/6/26.
 */

@Controller
@RequestMapping("/cart/")
public class CartController {
    @Autowired
    private ICartService iCartService;
    @RequestMapping("add.do")
    @ResponseBody
    public ResponseService<CartVo> add(HttpSession session, Integer count, Integer productId){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(currentUser.getId(),productId,count);

    }

    //更新购物车某个产品数量
    @RequestMapping("update.do")
    @ResponseBody
    public ResponseService<CartVo> upadte(HttpSession session,Integer count, Integer productId){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.update(currentUser.getId(),productId,count);

    }

    //想当于点击购物车图表示查询属于该用户id的所有购物车里面的商品
    @RequestMapping("list.do")
    @ResponseBody
    public ResponseService<CartVo> list(HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(currentUser.getId());

    }

    @RequestMapping("delete_product.do")
    @ResponseBody
    public ResponseService<CartVo> deleteProduct(HttpSession session, String productIds){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProduct(currentUser.getId(),productIds);

    }


    //全选
    //全反选

    //单独选
    //单独反选
    @RequestMapping("select_all.do")
    @ResponseBody
    public ResponseService<CartVo> selectAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.CHECKED);
    }

    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ResponseService<CartVo> unSelectAll(HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),null,Const.Cart.UN_CHECKED);
    }



    @RequestMapping("select.do")
    @ResponseBody
    public ResponseService<CartVo> select(HttpSession session,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.CHECKED);
    }

    @RequestMapping("un_select.do")
    @ResponseBody
    public ResponseService<CartVo> unSelect(HttpSession session,Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),productId,Const.Cart.UN_CHECKED);
    }


    //查询当前用户的购物车里面的产品数量,如果一个产品有10个,那么数量就是10.
    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public ResponseService<Integer> getCartProductCount(HttpSession session){

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user ==null){
            return ResponseService.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }










}
