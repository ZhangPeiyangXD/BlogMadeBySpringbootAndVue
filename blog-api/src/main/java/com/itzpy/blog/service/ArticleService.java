package com.itzpy.blog.service;


import com.itzpy.blog.vo.ArticleVo;
import com.itzpy.blog.vo.Result;
import com.itzpy.blog.vo.params.PageParams;

import java.util.List;

public interface ArticleService {
    static int HOT_ARTICLE_NUM = 5;

    /**
     * 分页查询 文章列表
     * @param pageParams
     * @return
     */
    Result listArticle(PageParams pageParams);


    /**
     * 获取最热文章
     * @param hotArticleNum 最热的文章数量
     * @return Result<List<HotArticleVo>>
     */
    Result hot(int hotArticleNum);
}
