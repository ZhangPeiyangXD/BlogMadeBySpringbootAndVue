package com.itzpy.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itzpy.blog.dao.dos.Archives;
import com.itzpy.blog.dao.pojo.Article;
import com.itzpy.blog.vo.ArticleVo;
import com.itzpy.blog.vo.HotArticleVo;
import com.itzpy.blog.vo.NewArticleVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    List<Archives> listArchives();

    IPage<Article> listArticle(Page<Article> page,
                               Long categoryId,
                               Long tagId,
                               String year,
                               String month);

    /**
     * 获取最热文章
     * @param hotArticleNum 最热的文章数量
     * @return Result<List<HotArticleVo>>
     */
    @Select("select id, title from article order by view_counts desc limit #{hotArticleNum}")
    List<HotArticleVo> getHotArticleByViewCount(int hotArticleNum);

    /**
     * 获取最新文章
     * @param newArticleNum 最新文章数量
     * @return Result<List<NewArticleVo>>
     */
    @Select("select id, title from article order by create_date desc limit #{newArticleNum}")
    List<NewArticleVo> getNewArticleByCreateDate(int newArticleNum);

    /**
     * 根据文章id查询文章
     * @param id 文章id
     * @return Result<ArticleVo>
     */
    @Select("select * from article where id = #{id}")
    Article getById(Long id);

    /**
     * 更新文章
     * @param article 文章
     * @return 影响的行数
     */
    int update(Article  article);
    
    /**
     * 更新文章评论数
     * @param articleId 文章ID
     * @return 影响的行数
     */
    @Update("UPDATE article SET comment_counts = comment_counts + 1 WHERE id = #{articleId}")
    int updateCommentCount(@Param("articleId") Long articleId);
    
    /**
     * 搜索文章标题包含特定字符串的文章
     * @param search 搜索关键词
     * @return 符合条件的文章列表
     */
    List<HotArticleVo> searchArticles(@Param("search") String search);
}