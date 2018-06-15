package com.mmall.controller.portal;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ResponseService;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * Created by jia on 2018/6/1.
 */
@Controller
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService iUserService;


    @RequestMapping(value ="login.do",method= RequestMethod.POST)
    @ResponseBody
    public ResponseService<User> login(String username, String password, HttpSession session){
        ResponseService<User> response = iUserService.login(username,password);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER ,response.getData());
        }

        return response;
    }
    //退出登录
    @RequestMapping(value ="logout.do",method= RequestMethod.POST)
    @ResponseBody
    public ResponseService<User> logout(HttpSession session){
        session.removeAttribute(Const.CURRENT_USER);
        return ResponseService.createBySuccess();
    }

    //注册 校验
    @RequestMapping( value = "regist.do", method = RequestMethod.POST)
    @ResponseBody
    public ResponseService<String> regist(User user){
        return iUserService.regist(user);

    }

    //校验用户名和邮箱是否存在 str 对应(用户名/邮箱)字符串的内容 type 对应 字段usernam / email
    @RequestMapping( value = "checkValid.do", method = RequestMethod.POST)
    @ResponseBody
    public ResponseService<String> checkValid(String str,String type){

        return iUserService.checkValid(str,type);

    }
    //获取用户登录信息
    @RequestMapping(value ="get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ResponseService<User> getUserInfo( HttpSession session){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user != null){
            user.setPassword("");
            return ResponseService.createBySuccess(user);
        }
        return ResponseService.createByErrorMessage("用户未登录,无法获取当前用户信息");
    }

    //密码提示问题的获取
    @RequestMapping(value ="forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ResponseService<String> getQuestion( String username){
        return iUserService.getQuestionByUserName(username);
    }
    //使用本地缓存检查问题和答案是否一致,并返回Data t String 的tocken 为了做对比
    @RequestMapping(value ="forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ResponseService<String> forgetCheckAnswer( String username ,String question,String answer){
        return iUserService.checkAnswer(username, question, answer);
    }
    //忘记密码中的重置密码 验证token 比对本地缓存 token 与传过来的token是否一致 一至就让修改密码
    @RequestMapping(value ="forget_reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ResponseService<String> forgetResetPassword( String username ,String passwordNew,String forgetToken){
        return iUserService.forgetResetPassword(username,passwordNew,forgetToken);
    }
    //登录状态的重置密码
    @RequestMapping(value ="reset_password.do",method = RequestMethod.POST)
    @ResponseBody
    public ResponseService<String> resetPassword(String passwordNew,String passwordOld,HttpSession session){

        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ResponseService.createBySuccessMessage("用户未登录,请登录!!!");
        }
        return iUserService.resetPassword(user,passwordNew,passwordOld);

    }

    //更新用户信息 用户ID和username 不能更改
    @RequestMapping(value ="update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ResponseService<User> updateInformation(HttpSession session,User user){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ResponseService.createBySuccessMessage("用户未登录,请登录!!!");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
       ResponseService<User> response= iUserService.updateInformation(user);
        if(response.isSuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    //获取用户的个人信息
    @RequestMapping(value ="get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ResponseService<User> getInformation(HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,无法获取当前用户信息,status=10,强制登录");
        }
        ResponseService<User> response = iUserService.getInformation(currentUser.getId());
        return response;
    }


}
