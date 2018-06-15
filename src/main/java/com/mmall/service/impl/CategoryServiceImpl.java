package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ResponseService;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jia on 2018/6/6.
 */

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
    //增加一行日志,如果查找的子节点返回的商品品类为空,打一行日志,不用返回u前端一个错误
    private Logger logger =  LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Autowired
    private CategoryMapper categoryMapper;

    public ResponseService addCategory (String categoryName,Integer parentId){
        if (StringUtils.isBlank(categoryName) || parentId == null){
            return ResponseService.createByErrorMessage("商品品类参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);//表示这个分类是可用的
        int count = categoryMapper.insert(category);
        if (count > 0 ){
            return ResponseService.createBySuccessMessage("添加商品品类成功");
        }
        return ResponseService.createByErrorMessage("添加失败");
    }

    @Override
    public ResponseService updateCategory(String categoryName, Integer categoryId) {
        if (StringUtils.isBlank(categoryName) || categoryId == null){
            return ResponseService.createByErrorMessage("商品品类参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setId(categoryId);
        int resultCount = categoryMapper.updateByPrimaryKey(category);
        if(resultCount > 0){
            return ResponseService.createBySuccessMessage("更新品类信息成功");
        }
        return ResponseService.createByErrorMessage("更新品类名字失败");
    }
    //获取品类子节点(平级)
    public ResponseService<List<Category>> getChildrenParallelCategory(Integer parentId){

            List<Category> list = categoryMapper.selectChildrenParallelCategoryByParentId(parentId);
            //判断本身是不是空,换判断是不是一个空集合
            if(CollectionUtils.isEmpty(list)){
                logger.info("未找到当前分类的子分类");
            }
            return ResponseService.createBySuccess(list);
    }

    /**
     * 递归查询本结点de ID 和孩子节点的ID
     * @param categoryId
     * @return
     */
    public ResponseService <List<Integer>>selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = new HashSet<>();
        findChildrenCategory(categorySet,categoryId);
        List<Integer> listId = new ArrayList<>();
        for (Category e:categorySet ){
            listId.add(e.getId());
        }
        return ResponseService.createBySuccess(listId);
    }

    //递归算法超出所有的子节点 set 集合借助hashcode 和equals 去重
    public Set<Category> findChildrenCategory(Set<Category> categorySet,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            categorySet.add(category);
            List<Category> categoryList = categoryMapper.selectChildrenParallelCategoryByParentId(categoryId);
            for (Category item :categoryList){
                findChildrenCategory(categorySet,item.getId());
            }
        }
        return categorySet;
    }
}
