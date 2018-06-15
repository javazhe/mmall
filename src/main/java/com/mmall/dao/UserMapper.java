package com.mmall.dao;

import com.mmall.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    int checkUserName(String username);

    int checkEmail(String email);

    int  checkEmailByUserName(@Param("userId") Integer userId, @Param("email") String email);

    User selectLogin(@Param("username") String username, @Param("password") String password);

    String selectQuestionByUserName(String username);

    int checkAnswer(@Param("username") String username, @Param("question") String question, @Param("answer") String answer);

    int updatePassword(@Param("username") String username, @Param("passwordNew") String passwordNew);

     int checkPassword(@Param("userId") Integer userId,@Param("passwordOld") String passwordOld);

}