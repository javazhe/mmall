package com.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ResponseService;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.util.DateTimeUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jia on 2018/6/8.
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;

     public ResponseService<String> saveOrUpadteProduct(Product product){
        if(product != null){

            if (StringUtils.isNotBlank(product.getSubImages())){
                String[] subImage = product.getSubImages().split(",");
                if (subImage.length > 0){
                     product.setMainImage(subImage[0]);
                }
            }

            if(product.getId() != null){
                int rowCount = productMapper.updateByPrimaryKeySelective(product);
                if (rowCount > 0){
                    return ResponseService.createBySuccessMessage("更新产品信息成功");
                }else{
                    return ResponseService.createByErrorMessage("更新产品信息失败");
                }
            }else{
                int rowCount = productMapper.insertSelective(product);
                if (rowCount > 0){
                    return ResponseService.createBySuccessMessage("添加产品信息成功");
                }else{
                    return ResponseService.createByErrorMessage("添加产品信息失败");
                }
            }

        }
          return ResponseService.createByCodeErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"新增和更新产品信息参数不正确");
     }

    public ResponseService<String> setSaleStatus(Integer productId ,Integer status){
        if (productId != null && status != null){

            Product product = new Product();
            product.setId(productId);
            product.setStatus(status);
            int rowCount = productMapper.updateByPrimaryKeySelective(product);
            if (rowCount > 0){
                return ResponseService.createBySuccessMessage("修改产品状态成功");
            }else{
                return ResponseService.createByErrorMessage("修改产品状态失败");
            }
        }
        return ResponseService.createByCodeErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),"修改产品状态信息参数不正确");
    }


    public ResponseService<ProductDetailVo> manageProductDetail(Integer productId){
        if (productId != null){
            Product product = productMapper.selectByPrimaryKey(productId);
            if(product == null){
                return ResponseService.createByErrorMessage("产品不存在或以下架");
            }
            ProductDetailVo productDetailVo = assembleProductDetailVo(product);
            return ResponseService.createBySuccess(productDetailVo);
        }
        return ResponseService.createByCodeErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
    }


    private  ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setName(product.getName());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImages(product.getSubImages());
        //imageHost通过读取配置文件set 值
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));

        //parentId
        Category category = categoryMapper.selectByPrimaryKey(productDetailVo.getCategoryId());
        if (category != null){
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        //creatTime OR updateTime dateUtils 实现时间的格式化 字符串的毫秒值到
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }

    public ResponseService<PageInfo> getProductList(Integer pageNum,Integer pageSize){
        //使用PageHelper 1.starPage 2. 填充自己的分页查询逻辑 3. pageHelper-收尾
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList = productMapper.selectProductList();
        List<ProductListVo> productVo = Lists.newArrayList();
        for (Product item : productList){
            ProductListVo productListVo =  assembleProductListVo(item);
            productVo.add(productListVo);

        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productVo);
        return ResponseService.createBySuccess(pageResult);
    }
    //组装 作用 :后台的分页不是POJO的每一项都要展示
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo  = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setStatus(product.getStatus());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));

        return productListVo;
    }
    //模糊查询并分页 使用ProductListVo 的包装类
    public ResponseService<PageInfo> searchProductByProductNameAndId(Integer pageNum,Integer pageSize,Integer productId ,String productName){
        PageHelper.startPage(pageNum,pageSize);
        List<Product> products = productMapper.selectByNameAndProductId(productId, productName);
        List<ProductListVo> productVo = Lists.newArrayList();
       for (Product item : products){
           ProductListVo productListVo = assembleProductListVo(item);
           productVo.add(productListVo);
       }
       PageInfo pageInfo = new PageInfo(products);
       pageInfo.setList(productVo);
       return ResponseService.createBySuccess(pageInfo);
    }

    public ResponseService<ProductDetailVo> productDetail(Integer productId){
        if (productId != null){
            Product product = productMapper.selectByPrimaryKey(productId);
            if(product == null){
                return ResponseService.createByErrorMessage("产品不存在或以下架");
            }
            if(product.getStatus() != Const.ProductEnumStatus.ON_SALE.getCode()){
                return ResponseService.createByErrorMessage("产品已下架");
            }

            ProductDetailVo productDetailVo = assembleProductDetailVo(product);
            return ResponseService.createBySuccess(productDetailVo);
        }
        return ResponseService.createByCodeErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
    }

    public ResponseService<PageInfo> getProductByKeywordCategoryId(Integer categoryId,String keyword,Integer pageNum,Integer pageSize,String orderBy){

        if (StringUtils.isBlank(keyword) & categoryId == null){
            return ResponseService.createByCodeErrorMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc()+"  关键字和Category不能都为空");
        }
        List<Integer> categoryIdList =  Lists.newArrayList();
        if (categoryId != null){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category== null && StringUtils.isBlank(keyword)){
                //没有该分类,并且还没有符合关键字,这个时候返回一个空的结果集,不报错,他不是一个错误
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> listvo = Lists.newArrayList();
                PageInfo pageinfo = new PageInfo(listvo);
                return ResponseService.createBySuccess(pageinfo);
            }

            categoryIdList = iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        if (StringUtils.isNotBlank(keyword)){
            keyword  = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNum,pageSize);
        //排序处理
        if (StringUtils.isNotBlank(orderBy)){
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" " + orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(categoryIdList.size() == 0 ? null : categoryIdList, StringUtils.isBlank(keyword) ? null : keyword);

        List<ProductListVo> productVo = Lists.newArrayList();
        for (Product item : productList){
            ProductListVo productListVo =  assembleProductListVo(item);
            productVo.add(productListVo);

        }
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productVo);
        return ResponseService.createBySuccess(pageResult);
    }

}
