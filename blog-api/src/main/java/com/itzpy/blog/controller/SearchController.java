package com.itzpy.blog.controller;

import com.itzpy.blog.aop.LogAnnotation;
import com.itzpy.blog.dao.pojo.Result;
import com.itzpy.blog.service.ArticleService;
import com.itzpy.blog.vo.HotArticleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private ArticleService articleService;

    /**
     * 搜索文章接口
     * @param search 搜索关键字
     * @return 搜索结果
     */
    @GetMapping("/article")
    @LogAnnotation(module = "搜索", operator = "搜索文章")
    public Result searchArticles(@RequestParam String search) {
        List<HotArticleVo> articles = articleService.searchArticles(search);
        return Result.success(articles);
    }
    
}