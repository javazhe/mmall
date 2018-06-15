package com.mmall.controller.portal;

import com.mmall.common.ResponseService;
import com.mmall.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by jia on 2018/6/11.
 */
@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    @RequestMapping("detail.do")
    @ResponseBody
    public ResponseService getDetail(Integer productId){
        return iProductService.productDetail(productId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ResponseService getProductListByCategoryIdAndKeyword(@RequestParam(value="categoryId",required = false) Integer categoryId,
                                          @RequestParam(value="keyword",required = false) String keyword,
                                          @RequestParam(value="pageNum",defaultValue = "1") Integer pageNum,
                                          @RequestParam(value="pageSize",defaultValue = "10") Integer pageSize,
                                          @RequestParam(value="orderBy",defaultValue = "") String orderBy){


    return iProductService.getProductByKeywordCategoryId(categoryId,keyword,pageNum,pageSize,orderBy);
    }
}
