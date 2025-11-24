package com.itzpy.blog.controller;


import com.alibaba.fastjson.spi.Module;
import com.itzpy.blog.aop.LogAnnotation;
import com.itzpy.blog.dao.pojo.ArticleMessage;
import com.itzpy.blog.service.ArticleService;
import com.itzpy.blog.utils.UserThreadLocal;
import com.itzpy.blog.dao.pojo.Result;
import com.itzpy.blog.vo.params.ArticleParam;
import com.itzpy.blog.vo.params.PageParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//json数据进行交互
@RestController
@RequestMapping("/articles")
@Slf4j
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    /**
     * 首页 文章列表
     *
     * @param pageParams 分页参数
     * @return result(文章列表)
     */
    @PostMapping
    @LogAnnotation(module = "文章", operator = "文章列表查询")
    public Result listArticle(@RequestBody PageParams pageParams) {
        return articleService.listArticle(pageParams);
    }
    

    /**
     * 最热文章
     * @return result(最热文章列表)
     */
    @PostMapping("/hot")
    @LogAnnotation(module = "文章", operator = "最热文章查询")
    public Result hotArticle() {
        return articleService.hotArticle(ArticleService.HOT_ARTICLE_NUM);
    }


    /**
     * 最新文章
     * @return result(最新文章列表)
     */
    @PostMapping("/new")
    @LogAnnotation(module = "文章", operator = "最新文章查询")
    public Result newArticle() {
        return articleService.newArticle(ArticleService.NEW_ARTICLE_NUM);
    }


    /**
     * 文章归档
     *
     * @return result(文章归档列表)
     */
    @PostMapping("/listArchives")
    @LogAnnotation(module = "文章", operator = "文章归档查询")
    public Result listArchives() {
        return articleService.listArchives();
    }


    /**
     * 查询文章详情
     *
     * @param id 文章id
     * @return result(文章详情)
     */
    @PostMapping("/view/{id}")
    @LogAnnotation(module = "文章", operator = "文章详情查询")
    public Result findArticleById(@PathVariable("id") Long id) {
        return articleService.findArticleById(id);
    }

    /**
     * 发布文章
     *
     * @param articleParam 文章信息
     * @return result(发布结果)
     */
    @PostMapping("/publish")
    @LogAnnotation(module = "文章", operator = "文章发布")
    public Result publish(@RequestBody ArticleParam articleParam) {
        return articleService.publish(articleParam);
    }
}