package com.mmall.service.impl;

import com.mmall.common.Const;
import com.mmall.common.ResponseService;
import com.mmall.common.TokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.UUID;

/**
 * Created by jia on 2018/6/1.
 */
@Service("iUserService")
public class UserServiceImpl  implements IUserService{
    @Autowired
    private UserMapper userMapper;
    @Override
    public ResponseService<User> login(String username, String password) {

        int count = userMapper.checkUserName(username);

        if(count == 0){
            return ResponseService.createByErrorMessage("用户名不存在");
        }
        // md5 加密
        String MD5PW = MD5Util.MD5EncodeUtf8(password);

        User user = userMapper.selectLogin(username,MD5PW);
        if(user == null){
            return ResponseService.createByErrorMessage("密码错误");
        }
        //密码质变为空为了安全
        user.setPassword(StringUtils.EMPTY);
        return ResponseService.createBySuccess("成功登录",user);
    }

    @Override
    public ResponseService<String> regist(User user) {
        //把校验邮箱和用户名的代码复用使用checkValid(str,type)
        ResponseService checkName = this.checkValid(user.getUsername(),Const.USERNAME);
        if(!checkName.isSuccess()){
            return checkName;
        }
        ResponseService checkEmail = this.checkValid(user.getEmail(),Const.EMAIL);
        if(!checkEmail.isSuccess()){
            return checkEmail;
        }
        /*//验证邮箱 用户名 存在不存在
        int count = userMapper.checkUserName(user.getUsername());
        if(count > 0){
            return ResponseService.createByErrorMessage("用户名已存在");
        }
        int emailU = userMapper.checkEmail(user.getEmail());
        if(emailU > 0){
            return ResponseService.createByErrorMessage("邮箱以被注册");
        }*/

        //到这的话就是可以注册的,把密码用Md5加密存入数据库中
        String md5password = MD5Util.MD5EncodeUtf8(user.getPassword());
        user.setPassword(md5password);
        //设置role 普通用户
        user.setRole(Const.Role.ROLE_CUSTOMER);

      int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return ResponseService.createByErrorMessage("注册失败");
        }
        return ResponseService.createBySuccessMessage("注册成功!");
    }

    @Override
    public ResponseService<String> checkValid(String str, String type) {
        if(StringUtils.isNotBlank(type)){
            if(Const.USERNAME.equals(type)){
                int count = userMapper.checkUserName(str);
                if(count > 0){
                    return ResponseService.createByErrorMessage("用户名已存在");
                }else{
                    return ResponseService.createBySuccessMessage("校验成功");
                }
            }
            if(Const.EMAIL.equals(type)){
                int emailU = userMapper.checkEmail(str);
                if(emailU > 0){
                    return ResponseService.createByErrorMessage("邮箱已被注册使用,请通过邮箱验证找回密码");
                }else{
                    return ResponseService.createBySuccessMessage("校验成功");
                }
            }
        }
        return ResponseService.createByErrorMessage("参数错误");
    }

    public ResponseService<String> getQuestionByUserName(String username){
        ResponseService checkName = this.checkValid(username,Const.USERNAME);
        if(!checkName.isSuccess()){
            String question = userMapper.selectQuestionByUserName(username);
            if(StringUtils.isNotBlank(question)){
                return ResponseService.createBySuccess(question);
            }else{
                return ResponseService.createByErrorMessage("用户未设置找回密码的问题");
            }
        }
        return ResponseService.createByErrorMessage("用户名无效,不存在该用户");
    }

    public ResponseService<String> checkAnswer(String username, String question,String answer){
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if(resultCount > 0){
            //说明问题好答案都是这个用户的,并且是答案是正确的 ,然后把forgetTocken放在本地缓存中并设置一个有效期 在TockenCache
            String forgetTocken = UUID.randomUUID().toString();
            TokenCache.setKey("token_"+username ,forgetTocken);
            return ResponseService.createBySuccess(forgetTocken);
        }
        return ResponseService.createByErrorMessage("问题的答案错误");
    }

    @Override
    public ResponseService<String> forgetResetPassword(String username, String passwordNew, String forgetToken) {
        if(StringUtils.isBlank(forgetToken)){
            return ResponseService.createByErrorMessage("参数错误,token需要传递");
        }
        ResponseService checkValid = this.checkValid(username,Const.USERNAME);
        if(checkValid.isSuccess()){
            return ResponseService.createByErrorMessage("用户名不存在");
        }
        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX+ username);
        if(StringUtils.isBlank(token)){
            return ResponseService.createByErrorMessage("token无效或者过期了");
        }
        if(StringUtils.equals(forgetToken,token)){
            //想等的话就可以让修改密码 MD5加密一下
            String md5pw = MD5Util.MD5EncodeUtf8(passwordNew);
            int countResult = userMapper.updatePassword(username,md5pw);
            if(countResult > 0){
                return ResponseService.createBySuccessMessage("密码修改成功");
            }

        }else {
            return ResponseService.createByErrorMessage("token不一致,请重新获取token");
        }
        return ResponseService.createByErrorMessage("密码修改失败");
    }

    public ResponseService<String> resetPassword(User user,String passwordNew,String passwordOld){
        //防止横向越权,要确保这个用户的旧密码是这个用户的 校验一下
        int count = userMapper.checkPassword(user.getId(),MD5Util.MD5EncodeUtf8(passwordOld));
        if(count == 0){
            return ResponseService.createByErrorMessage("输入的旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        int resultCount = userMapper.updateByPrimaryKey(user);
        if(resultCount > 0){
            return ResponseService.createBySuccessMessage("密码修改成功");
        }
        return ResponseService.createByErrorMessage("密码修改失败");
    }

    public ResponseService<User> updateInformation(User user){
        //更新的时候检查更新后邮箱的有效性 username 和 userid 都不能被更新
        int count =  userMapper.checkEmailByUserName(user.getId(),user.getEmail());
        if (user != null ) {
            if (count > 0) {
                return ResponseService.createByErrorMessage("邮箱已被使用,请重新更换邮箱");
            }
            User updateUser =  new User();
            updateUser.setId(user.getId());
            updateUser.setUsername(user.getUsername());
            updateUser.setEmail(user.getEmail());
            updateUser.setPhone(user.getPhone());
            updateUser.setQuestion(user.getQuestion());
            updateUser.setAnswer(user.getAnswer());
            int resultCount = userMapper.updateByPrimaryKeySelective(updateUser);
            if(resultCount > 0){
                ResponseService<User> alerdyUpdate= this.getInformation(updateUser.getId());
                return ResponseService.createBySuccess("更新个人信息成功",alerdyUpdate.getData());
            }
        }
        return ResponseService.createByErrorMessage("更新信息失败");
    }

    @Override
    public ResponseService<User> getInformation(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return ResponseService.createByErrorMessage("该用户不存在");
        }
        user.setPassword("");
        return ResponseService.createBySuccess(user);
    }

    //backend 校验是不是管理员用户
    public ResponseService<String> checkAdminRole(User user){
        if (user != null ){
            if(user.getRole() == Const.Role.ROLE_ADMIN){
                return ResponseService.createBySuccessMessage("校验成功,是管理员用户!");
            }
        }
        return ResponseService.createByErrorMessage("不是管理员用户,没有权限访问");
    }
}
