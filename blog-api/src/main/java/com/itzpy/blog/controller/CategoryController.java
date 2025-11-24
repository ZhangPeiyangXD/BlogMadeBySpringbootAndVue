package com.itzpy.blog.controller;

import com.itzpy.blog.aop.LogAnnotation;
import com.itzpy.blog.dao.pojo.Result;
import com.itzpy.blog.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/categorys")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查询所有分类
     * @return Result 所有分类列表
     */
    @GetMapping()
    @LogAnnotation(module = "分类", operator = "获取分类列表")
    public Result categories() {
        return categoryService.findAll();
    }


    /**
     * 查询所有文章分类
     * @return Result 所有文章分类列表
     */
    @GetMapping("/detail")
    @LogAnnotation(module = "分类", operator = "获取所有文章分类")
    public Result findAllDetail() {
        return categoryService.findAllDetail();
    }

}
