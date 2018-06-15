package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ResponseService;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpSession;

/**
 * Created by jia on 2018/6/6.
 */
@Controller
@RequestMapping("/manage/category/")
public class CategoryManageController {
    @Autowired
    private IUserService  iUserService;
    @Autowired
    private ICategoryService iCategoryService;
    @RequestMapping(value = "add_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ResponseService addCategory(String categoryName, @RequestParam(value="parentId",defaultValue = "0") Integer parentId, HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return  ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登陆");
        }else{
            //校验是不是管理员用户
            ResponseService<String> response = iUserService.checkAdminRole(currentUser);
            if (!response.isSuccess()){
                return response;
            }
        }
        return iCategoryService.addCategory(categoryName,parentId);
    }

    @RequestMapping(value = "set_category_name.do", method = RequestMethod.GET)
    @ResponseBody
    public ResponseService setCategoryName(String categoryName, Integer categoryId,HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return  ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登陆");
        }else{
            //校验是不是管理员用户
            if (!iUserService.checkAdminRole(currentUser).isSuccess()){
                return iUserService.checkAdminRole(currentUser);
            }
        }
        return iCategoryService.updateCategory(categoryName,categoryId);
    }

    @RequestMapping(value = "get_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ResponseService getChildrenParallelCategory(@RequestParam(value = "parentId",defaultValue = "0") Integer parentId,HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return  ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登陆");
        }
            //校验是不是管理员用户
            if (iUserService.checkAdminRole(currentUser).isSuccess()){
                return iCategoryService.getChildrenParallelCategory(parentId);
            }else{
                return iUserService.checkAdminRole(currentUser);
            }
    }

    @RequestMapping(value = "get_deep_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ResponseService selectCategoryAndChildrenById(Integer categoryId,HttpSession session){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser == null){
            return  ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录,请登陆");
        }
        //校验是不是管理员用户
        if (iUserService.checkAdminRole(currentUser).isSuccess()){
            return iCategoryService.selectCategoryAndChildrenById(categoryId);
        }else{
            return iUserService.checkAdminRole(currentUser);
        }
    }
}
