package com.itzpy.blog.dao.mapper;

import com.itzpy.blog.dao.pojo.ArticleTag;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleTagMapper {

    /**
     * 插入文章标签
     * @param articleTag
     */
    @Insert("insert into article_tag (article_id,tag_id) values (#{articleId},#{tagId})")
    public void insert(ArticleTag articleTag);
}
