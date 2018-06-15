package com.mmall.service;

import com.mmall.common.ResponseService;
import com.mmall.pojo.User;



/**
 * Created by jia on 2018/6/1.
 */
public interface IUserService {

    ResponseService<User> login(String username, String password);

    ResponseService<String> regist(User user);

    ResponseService<String> checkValid(String str,String type);

    ResponseService<String> getQuestionByUserName(String username);

    ResponseService<String> checkAnswer(String username, String question,String answer);

    ResponseService<String> forgetResetPassword( String username ,String passwordNew,String forgetToken);

    ResponseService<String> resetPassword(User user,String passwordNew,String passwordOld);

    ResponseService<User> updateInformation(User user);

    ResponseService<User> getInformation(Integer userId);

    ResponseService<String> checkAdminRole(User user);

}
