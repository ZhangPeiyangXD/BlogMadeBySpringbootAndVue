package com.itzpy.blog.controller;


import com.itzpy.blog.service.ArticleService;
import com.itzpy.blog.utils.UserThreadLocal;
import com.itzpy.blog.dao.pojo.Result;
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
    

    /**
     * 最热文章
     * @return result(最热文章列表)
     */
    @PostMapping("/hot")
    public Result hotArticle() {
        log.info("查询最热文章:");

        // 安全地打印用户信息（如果存在）
        if (UserThreadLocal.get() != null) {
            System.out.println(UserThreadLocal.get().toString());
        } else {
            System.out.println("当前没有登录用户");
        }

        return articleService.hotArticle(ArticleService.HOT_ARTICLE_NUM);
    }


    /**
     * 最新文章
     * @return result(最新文章列表)
     */
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


    /**
     * 查询文章详情
     *
     * @param id 文章id
     * @return result(文章详情)
     */
    @PostMapping("/view/{id}")
    public Result findArticleById(@PathVariable("id") Long id) {
        log.info("查询文章详情:{}", id);

        return articleService.findArticleById(id);
    }
}