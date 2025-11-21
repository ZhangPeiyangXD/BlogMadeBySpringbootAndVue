package com.itzpy.blog.controller;


import com.itzpy.blog.service.ArticleService;
import com.itzpy.blog.vo.Result;
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
    public Result listArticle(@RequestBody PageParams pageParams) {
        log.info("文章列表分页查询:{}", pageParams);

        return articleService.listArticle(pageParams);
    }


    @PostMapping("/hot")
    public Result hotArticle() {
        log.info("查询最热文章:");

        return articleService.hotArticle(ArticleService.HOT_ARTICLE_NUM);
    }


    @PostMapping("/new")
    public Result newArticle() {
        log.info("查询最新文章:");

        return articleService.newArticle(ArticleService.NEW_ARTICLE_NUM);
    }


    /**
     * 文章归档
     *
     * @return result(文章归档列表)
     */
    @PostMapping("/listArchives")
    public Result listArchives() {
        log.info("查询文章归档:");

        return articleService.listArchives();
    }
}
