package com.mmall.controller.backend;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ResponseService;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by jia on 2018/6/8.
 */
@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IFileService iFileServic;

    @RequestMapping("save.do")
    @ResponseBody
    public ResponseService saveOrUpdateProduct(HttpSession session, Product product){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //校验是不是管理员用户
        if (iUserService.checkAdminRole(currentUser).isSuccess()){
            return iProductService.saveOrUpadteProduct(product);
        }else{
            return iUserService.checkAdminRole(currentUser);
        }

    }

    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ResponseService setSaleStatus(HttpSession session, Integer productId,Integer status){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //校验是不是管理员用户
        if (iUserService.checkAdminRole(currentUser).isSuccess()){
            return iProductService.setSaleStatus(productId,status);
        }else{
            return iUserService.checkAdminRole(currentUser);
        }
    }


    //获取产品详情的接口
    @RequestMapping("detail.do")
    @ResponseBody
    public ResponseService getDetail(HttpSession session, Integer productId){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //校验是不是管理员用户
        if (iUserService.checkAdminRole(currentUser).isSuccess()){
            return iProductService.manageProductDetail(productId);
        }else{
            return iUserService.checkAdminRole(currentUser);
        }
    }

    //后台产品分页接口
    @RequestMapping("list.do")
    @ResponseBody
    public ResponseService getDetail(HttpSession session, @RequestParam(value ="pageNum",defaultValue = "1") Integer pageNum ,@RequestParam(value ="pageSize",defaultValue = "10") Integer pageSize){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //校验是不是管理员用户
        if (iUserService.checkAdminRole(currentUser).isSuccess()){
            return iProductService.getProductList(pageNum, pageSize);
        }else{
            return iUserService.checkAdminRole(currentUser);
        }
    }

    //后台产品产品搜索接口
    @RequestMapping("search.do")
    @ResponseBody
    public ResponseService searchProduct(HttpSession session, @RequestParam(value ="pageNum",defaultValue = "1") Integer pageNum ,@RequestParam(value ="pageSize",defaultValue = "10")Integer pageSize, Integer productId ,String productName){
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //校验是不是管理员用户
        if (iUserService.checkAdminRole(currentUser).isSuccess()){
            return iProductService.searchProductByProductNameAndId(pageNum,pageSize,productId,productName);
        }else{
            return iUserService.checkAdminRole(currentUser);
        }
    }
    @RequestMapping("upload.do")
    @ResponseBody
    public ResponseService upload(HttpSession session,HttpServletRequest request,@RequestParam(value = "upload_file",required = false) MultipartFile file){

        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //校验是不是管理员用户
        if (iUserService.checkAdminRole(currentUser).isSuccess()){
            //根据HttpServletRequest 上下文动态的获取一个相对路径
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileServic.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);
            return ResponseService.createBySuccess(fileMap);
        }else{
            return iUserService.checkAdminRole(currentUser);
        }
    }
    //simditor富文本上传对返回只有自己的要求  富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
     //      { "success": true/false,
     //       "msg": "error message", # optional
     //         "file_path": "[real file path]"
    //        }

    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richTextImgUpload(HttpSession session, HttpServletRequest request, @RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletResponse response){
        Map resultMap = Maps.newHashMap();
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null){
            resultMap.put("success",false);
            resultMap.put("msg","未登录");
           return resultMap;
        }

        if (iUserService.checkAdminRole(currentUser).isSuccess()){
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileServic.upload(file,path);
            if (StringUtils.isBlank(targetFileName)){
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;
            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path","url");
            //修改一下responsede header   simditor  文档规范
           response.addHeader("Access_Control-Allow-Headers","X-File-Name");
            return resultMap;
        }else{
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }

        }
}
