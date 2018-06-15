package com.mmall.service;

import com.mmall.common.ResponseService;
import com.mmall.pojo.Category;

import java.util.List;

/**
 * Created by jia on 2018/6/6.
 */
public interface ICategoryService {
    ResponseService<String> addCategory (String categooryName, Integer parentId);

    ResponseService updateCategory(String categoryName, Integer categoryId);

    ResponseService<List<Category>> getChildrenParallelCategory(Integer parentId);

    ResponseService<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
    
}
