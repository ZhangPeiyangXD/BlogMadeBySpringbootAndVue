package com.itzpy.blog.dao.mapper;

import com.itzpy.blog.vo.ArticleBodyVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArticleBodyMapper {

    @Select("select content from article_body where id = #{bodyId}")
    ArticleBodyVo selectContentById(Long bodyId);
}
