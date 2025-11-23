package com.itzpy.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itzpy.blog.dao.dos.Archives;
import com.itzpy.blog.dao.mapper.ArticleBodyMapper;
import com.itzpy.blog.dao.mapper.ArticleMapper;
import com.itzpy.blog.dao.mapper.CategoryMapper;
import com.itzpy.blog.dao.pojo.Article;
import com.itzpy.blog.service.ArticleService;
import com.itzpy.blog.service.SysUserService;
import com.itzpy.blog.service.TagService;
import com.itzpy.blog.service.ThreadService;
import com.itzpy.blog.vo.*;
import com.itzpy.blog.vo.params.PageParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private TagService tagService;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ArticleBodyMapper articleBodyMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 分页查询文章列表
     *
     * @param pageParams  分页参数
     * @return result(文章列表)
     */
    @Override
    public Result listArticle(PageParams pageParams) {
        //分页查询article数据库表
        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();

        //按照创建日期和权重进行排序
        queryWrapper.orderByDesc(Article::getWeight, Article::getCreateDate);
        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);

        List<ArticleVo> articleVos = copyList(articlePage.getRecords(), true, true);

        return Result.success(articleVos);
    }


    /**
     * 查询最热文章
     * @param hotArticleNum 最热的文章数量
     * @return result(最热文章列表)
     */
    @Override
    public Result hotArticle(int hotArticleNum) {
        List<HotArticleVo> hotArticleVoList = new ArrayList<>();
        //只封装id和title
        hotArticleVoList = articleMapper.getHotArticleByViewCount(hotArticleNum);

        return Result.success(hotArticleVoList);
    }


    /**
     * 获取最新文章
     * @param newArticleNum 最新文章数量
     * @return result(最新文章列表)
     */
    @Override
    public Result newArticle(int newArticleNum) {
        List<NewArticleVo> newArticleVoList = new ArrayList<>();
        //只封装id和title
        newArticleVoList = articleMapper.getNewArticleByCreateDate(newArticleNum);

        return Result.success(newArticleVoList);
    }


    /**
     * 获取文章归档
     * @return result(文章归档列表)
     */
    @Override
    public Result listArchives() {
        List<Archives> archivesList = articleMapper.listArchives();

        return Result.success(archivesList);
    }

    /**
     * 查询文章详情
     *
     * @param id 文章id
     * @return result(文章详情)
     */
    @Override
    public Result findArticleById(Long id) {
        //1.根据id查询文章
        //2.根据bodyId和categoryId做关联查询(都在copy方法中实现了)
        Article article = articleMapper.getById(id);
        ArticleVo articleVo =  copy(article, true, true,true, true);

        //3.增加阅读数(在线程池中执行)
        threadService.updateArticleViewCount(articleMapper, article);

        return Result.success(articleVo);
    }


    /**
     * 将list<article>转换成vo list<articleVo>
     *
     * @param records  list<article>
     * @return list<articleVo>
     */
    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record, isTag, isAuthor, isBody, isCategory));
        }
        return articleVoList;
    }

    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record, isTag, isAuthor, false, false));
        }
        return articleVoList;
    }



    /**
     * 将article对象转换成articleVo对象,通过articleId获取标签信息，作者信息
     *
     * @param article  article对象
     * @param isTag  有无标签信息
     * @param isAuthor  有无作者信息
     * @return articleVo
     */
    private ArticleVo copy(Article article, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory) {
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article, articleVo);

        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));

        Long articleId = article.getId();
        Long categoryId = article.getCategoryId();
        Long bodyId = article.getBodyId();
        Long authorId = article.getAuthorId();
        //判断接口是否需要额外信息
        if(isTag){
            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if(isAuthor){
            articleVo.setAuthor(sysUserService.findUserById(authorId).getNickname());
        }
        if(isBody){
            articleVo.setBody(articleBodyMapper.selectContentById(bodyId));
        }
        if(isCategory){
            articleVo.setCategory(categoryMapper.findCategoryById(categoryId));
        }
        return articleVo;
    }
}
