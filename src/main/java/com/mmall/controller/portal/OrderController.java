package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ResponseService;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by jia on 2018/11/15.
 */
@Controller
@RequestMapping("/order/")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;
    @RequestMapping(value = "pay.do",method = RequestMethod.GET)
    @ResponseBody
    public ResponseService pay(HttpSession session,Long orderNo, HttpServletRequest resquest){

        User currentUser =(User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //获取到上传文件的上下文,为了拿到持久化以后的二维码文件图片,返回给前台,前台展示,用户扫码支付
        String path = resquest.getSession().getServletContext().getRealPath("upload");
        return iOrderService.pay(currentUser.getId(),orderNo,path);
    }

    @RequestMapping(value = "alipay_callback.do",method = RequestMethod.POST)
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String,String> params = Maps.newHashMap();
        for (Iterator iter = parameterMap.keySet().iterator();iter.hasNext();){
            String  name = (String)iter.next();
            String[] values = (String[])parameterMap.get(name);
            String valuesStr = "";
            for (int i = 0; i<values.length;i++){
                valuesStr = (i == values.length - 1) ?  valuesStr+values[i] :  valuesStr+values[i]+ "";
            }
            params.put(name,valuesStr);
        }
        log.info("支付宝回调,sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),params.toString());
        //验证回调的正确性,看是不是支付宝发的,是不是重复的发送通知
        //https://docs.open.alipay.com/194/103296/ 文档

        params.remove("sign_type");
        try {
            boolean alipayRsaCheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!alipayRsaCheckedV2) {
                log.info("非法请求,验证不通过,验签失败");
                return ResponseService.createByErrorMessage("非法请求,验证不通过,验签失败");
            }
        } catch (AlipayApiException e) {
            log.error("支付宝验证回调异常",e);
        }


        //// TODO: 2018/11/20 业务逻辑参数校验

        ResponseService responseService = iOrderService.alipayCallback(params);
        if (responseService.isSuccess()){

            return Const.AlipayCallback.RESPONSE_SUCCESS;


        }
        return Const.AlipayCallback.RESPONSE_FAILED;

    }

    //前台轮询付款是否成功
    @RequestMapping(value = "query_order_pay_status.do",method = RequestMethod.GET)
    @ResponseBody
    public ResponseService alipayCallback(HttpSession session,Long orderNo){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        ResponseService responseService = iOrderService.queryByUserIdOrderNo(currentUser.getId(), orderNo);
        if (responseService.isSuccess()){
            return ResponseService.createBySuccess(true);
        }
        return ResponseService.createBySuccess(false);

    }

}
