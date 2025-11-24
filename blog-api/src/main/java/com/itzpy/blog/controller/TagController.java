package com.itzpy.blog.controller;

import com.itzpy.blog.aop.LogAnnotation;
import com.itzpy.blog.service.TagService;
import com.itzpy.blog.dao.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tags")
@Slf4j
public class TagController {
    private static final int HOT_TAG_NUM = 6;
    @Autowired
    private TagService tagService;

    @GetMapping("/hot")
    @LogAnnotation(module = "标签", operator = "获取热门标签")
    public Result hot() {
        return tagService.hot(HOT_TAG_NUM);
    }


    /**
     * 查询所有标签
     * @return Result<List<TagVo>> 标签列表
     */
    @GetMapping
    @LogAnnotation(module = "标签", operator = "获取所有标签")
    public Result findAll() {
        return tagService.findAll();
    }


    @GetMapping("/detail")
    @LogAnnotation(module = "标签", operator = "获取所有标签详情")
    public Result findAllDetail() {
        return tagService.findAllDetail();
    }

}
