package com.itzpy.blog.service.impl;

import com.itzpy.blog.dao.mapper.CategoryMapper;
import com.itzpy.blog.dao.pojo.Category;
import com.itzpy.blog.dao.pojo.Result;
import com.itzpy.blog.service.CategoryService;
import com.itzpy.blog.vo.CategoryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Result findAll() {
        List<Category> categorieList = categoryMapper.findAll();

        return Result.success(copyList(categorieList));
    }

    @Override
    public Result findAllDetail() {
        List<Category> categorieList = categoryMapper.findAll();

        return Result.success(copyList(categorieList));
    }


    public CategoryVo copy(Category category){
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        categoryVo.setId(String.valueOf(category.getId()));
        return categoryVo;
    }

    public List<CategoryVo> copyList(List<Category> categoryList){
        List<CategoryVo> categoryVoList = new ArrayList<>();
        for (Category category : categoryList) {
            categoryVoList.add(copy(category));
        }
        return categoryVoList;
    }
}
