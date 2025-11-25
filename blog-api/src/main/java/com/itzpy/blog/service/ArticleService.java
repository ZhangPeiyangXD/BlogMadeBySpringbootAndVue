package com.itzpy.blog.service;


import com.itzpy.blog.dao.pojo.Result;
import com.itzpy.blog.vo.params.ArticleParam;
import com.itzpy.blog.vo.params.PageParams;

public interface ArticleService {
    static int HOT_ARTICLE_NUM = 5;
    static int NEW_ARTICLE_NUM = 5;

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
    Result hotArticle(int hotArticleNum);


    /**
     * 获取最新文章
     * @param newArticleNum 最新文章数量
     * @return Result<List<NewArticleVo>>
     */
    Result newArticle(int newArticleNum);


    /**
     * 获取文章归档
     * @return Result<List<ArchiveVo>>
     */
    Result listArchives();


    /**
     * 根据文章id查询文章
     * @param id 文章id
     * @return Result<ArticleVo>
     */
    Result findArticleById(Long id);


    /**
     * 发布文章
     * @param articleParam 文章参数
     * @return Result<articleId>
     */
    Result publish(ArticleParam articleParam);
    
    /**
     * 删除文章
     * @param articleId 文章ID
     * @return Result
     */
    Result delete(Long articleId);
    
    /**
     * 修改文章
     * @param articleId 文章ID
     * @return Result
     */
    Result change(Long articleId);
}