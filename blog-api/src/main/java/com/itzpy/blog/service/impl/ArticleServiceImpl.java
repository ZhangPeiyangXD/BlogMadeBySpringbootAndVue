package com.itzpy.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itzpy.blog.dao.dos.Archives;
import com.itzpy.blog.dao.mapper.*;
import com.itzpy.blog.dao.pojo.*;
import com.itzpy.blog.service.ArticleService;
import com.itzpy.blog.service.SysUserService;
import com.itzpy.blog.service.TagService;
import com.itzpy.blog.service.ThreadService;
import com.itzpy.blog.utils.UserThreadLocal;
import com.itzpy.blog.vo.*;
import com.itzpy.blog.vo.params.ArticleParam;
import com.itzpy.blog.vo.params.PageParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private TagService tagService;
    @Autowired
    private ThreadService threadService;
    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleBodyMapper articleBodyMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    ArticleTagMapper articleTagMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Value("${authorId}")
    private Long authorId;

    /**
     * 分页查询文章列表
     *
     * @param pageParams  分页参数
     * @return result(文章列表)
     */
    /*@Override
    public Result listArticle(PageParams pageParams) {
        //分页查询article数据库表
        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();

        //按分类查询时传入CategoryId（不传递则展示所有分类对应所有文章）
        if(pageParams.getCategoryId() != null){
            queryWrapper.eq(Article::getCategoryId, pageParams.getCategoryId());
        }

        //按标签查询时传入TagId（不传递则展示所有标签对应所有文章）
        List<Long> articleIdList = new ArrayList<>();
        if(pageParams.getTagId() != null){
            LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
            articleTagLambdaQueryWrapper.eq(ArticleTag::getTagId, pageParams.getTagId());

            // 把文章列表转换成文章id的集合
            List<ArticleTag> articleTagList = articleTagMapper.selectList(articleTagLambdaQueryWrapper);
            for(ArticleTag articleTag : articleTagList){
                articleIdList.add(articleTag.getArticleId());
            }

            // 加入到查询条件中
            if(!articleIdList.isEmpty()){
                queryWrapper.in(Article::getId, articleIdList);
            }
        }

        //按照创建日期和权重进行排序
        queryWrapper.orderByDesc(Article::getWeight, Article::getCreateDate);
        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);

        List<ArticleVo> articleVos = copyList(articlePage.getRecords(), true, true);

        return Result.success(articleVos);
    }
*/
    @Override
    public Result listArticle(PageParams pageParams) {
        Page<Article> page = new Page<>(pageParams.getPage(),pageParams.getPageSize());

        IPage<Article> articleIPage = articleMapper.listArticle(
                page,
                pageParams.getCategoryId(),
                pageParams.getTagId(),
                pageParams.getYear(),
                pageParams.getMonth());
        List<Article> records = articleIPage.getRecords();
        for (Article record : records) {
            String viewCount = (String) redisTemplate.opsForHash().get("view_count", String.valueOf(record.getId()));
            if (viewCount != null){
                record.setViewCounts(Integer.parseInt(viewCount));
            }
        }
        return Result.success(copyList(records,true,true));
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
     * 发布文章
     *
     * @param articleParam 文章参数
     * @return result(发布结果)
     */
    @Transactional
    @Override
    public Result publish(ArticleParam articleParam) {
        // 获取当前登录用户
        SysUser sysUser = UserThreadLocal.get();
        
        Article article = new Article();
        // 设置作者id为当前登录用户ID，如果未登录则使用默认ID
        if (sysUser != null) {
            article.setAuthorId(sysUser.getId());
        } else {
            article.setAuthorId(authorId);
        }

        // 插入文章表，获取文章id
        articleMapper.insert(article);
        Long articleId = article.getId();

        //设置权重，观看量，标题，简介, 评论，创建时间
        article.setWeight(Article.Article_Common);
        article.setViewCounts(0);
        article.setTitle(articleParam.getTitle());
        article.setSummary(articleParam.getSummary());
        article.setCommentCounts(0);
        article.setCreateDate(System.currentTimeMillis());
        // 修复可能的null值问题
        if (articleParam.getCategory() != null && articleParam.getCategory().getId() != null) {
            article.setCategoryId(articleParam.getCategory().getId());
        } else {
            article.setCategoryId(1L); // 设置默认分类ID
        }

        //标签：
        List<TagVo> tagVoList = articleParam.getTags();
        if (tagVoList != null) {
            for(TagVo tagVo : tagVoList){
                // 修复可能的null值问题
                if (tagVo.getId() != null) {
                    ArticleTag articleTag = new ArticleTag();
                    articleTag.setArticleId(articleId);
                    articleTag.setTagId(tagVo.getId());
                    articleTagMapper.insert(articleTag);
                }
            }
        }

        // 文章内容：
        ArticleBody articleBody = new ArticleBody();

        articleBody.setArticleId(articleId);
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());

        articleBodyMapper.insert(articleBody);
        //获取文章主键Id值
        article.setBodyId(articleBody.getId());

        //更新文章
        articleMapper.update(article);

        Map<String, String> map = new HashMap<>();
        map.put("id", articleId.toString());
        return Result.success(map);
    }

    @Override
    public Result delete(Long articleId) {
        // 删除文章相关数据
        articleMapper.deleteById(articleId);
        articleBodyMapper.deleteByArticleId(articleId);
        articleTagMapper.deleteByArticleId(articleId);
        commentMapper.deleteCommentsByArticleId(articleId);
        return Result.success(null);
    }

    @Override
    public Result change(Long articleId) {
        // 获取文章详情
        Article article = articleMapper.selectById(articleId);
        if (article == null) {
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        return Result.success(article);
    }

    @Override
    public List<HotArticleVo> searchArticles(String search) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Article::getTitle, search);
        queryWrapper.last("LIMIT 5");
        List<Article> articles = articleMapper.selectList(queryWrapper);
        
        List<HotArticleVo> articleVos = new ArrayList<>();
        for (Article article : articles) {
            HotArticleVo hotArticleVo = new HotArticleVo();
            hotArticleVo.setId(article.getId());
            hotArticleVo.setTitle(article.getTitle());
            articleVos.add(hotArticleVo);
        }
        return articleVos;
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

    /**
     * 将list<article>转换成vo list<articleVo>
     *
     * @param records  list<article>
     * @return list<articleVo>
     */
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