package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.ResponseService;
import com.mmall.pojo.Product;
import com.mmall.vo.ProductDetailVo;

/**
 * Created by jia on 2018/6/8.
 */
public interface IProductService {
    ResponseService<String> saveOrUpadteProduct(Product product);

    ResponseService<String> setSaleStatus(Integer productId ,Integer status);

    ResponseService<ProductDetailVo> manageProductDetail(Integer productId);

    ResponseService<PageInfo> getProductList(Integer pageNum, Integer pageSize);

    ResponseService<PageInfo> searchProductByProductNameAndId(Integer pageNum, Integer pageSize, Integer productId, String productName);


    ResponseService<ProductDetailVo> productDetail(Integer productId);

    ResponseService<PageInfo> getProductByKeywordCategoryId(Integer categoryId, String keyword, Integer pageNum, Integer pageSize, String orderBy);
}
