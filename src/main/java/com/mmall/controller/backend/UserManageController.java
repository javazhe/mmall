package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseService;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by jia on 2018/6/4.
 */
@Controller
@RequestMapping("/manage/user/")
public class UserManageController {
    @Autowired
    private IUserService iUserService;
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ResponseService<User> login(String username, String password, HttpSession session){
        ResponseService<User> response = iUserService.login(username,password);

        if(response.isSuccess()){
            if(response.getData().getRole() == Const.Role.ROLE_ADMIN){
                session.setAttribute(Const.CURRENT_USER ,response.getData());
                return response;
            }else{
                return ResponseService.createByErrorMessage("不是管理员无法登录");
            }

        }
        return response;
    }
}
