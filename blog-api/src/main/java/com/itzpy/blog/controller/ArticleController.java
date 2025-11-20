package com.itzpy.blog.controller;


import com.itzpy.blog.service.ArticleService;
import com.itzpy.blog.vo.Result;
import com.itzpy.blog.vo.params.ArticleParam;
import com.itzpy.blog.vo.params.PageParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//json数据进行交互
@RestController
@RequestMapping("articles")
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
}
