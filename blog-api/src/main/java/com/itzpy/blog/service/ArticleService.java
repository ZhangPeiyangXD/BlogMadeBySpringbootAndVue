package com.itzpy.blog.service;


import com.itzpy.blog.vo.Result;
import com.itzpy.blog.vo.params.PageParams;

public interface ArticleService {
    /**
     * 分页查询 文章列表
     * @param pageParams
     * @return
     */
    Result listArticle(PageParams pageParams);

}
