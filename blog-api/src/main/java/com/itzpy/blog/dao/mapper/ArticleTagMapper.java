package com.itzpy.blog.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itzpy.blog.dao.pojo.ArticleTag;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {

    /**
     * 插入文章标签
     * @param articleTag
     */
    @Insert("insert into article_tag (article_id,tag_id) values (#{articleId},#{tagId})")
    public int insert(ArticleTag articleTag);
    
    /**
     * 根据文章ID删除文章标签关联
     * @param articleId 文章ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM article_tag WHERE article_id = #{articleId}")
    int deleteByArticleId(@Param("articleId") Long articleId);
}