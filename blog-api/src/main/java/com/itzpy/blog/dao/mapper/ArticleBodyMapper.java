package com.itzpy.blog.dao.mapper;

import com.itzpy.blog.dao.pojo.ArticleBody;
import com.itzpy.blog.vo.ArticleBodyVo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ArticleBodyMapper {

    @Select("select content from article_body where id = #{bodyId}")
    ArticleBodyVo selectContentById(Long bodyId);

    /**
     * 插入文章内容
     * @param articleBody
     */
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    @Insert("insert into article_body (article_id, content,content_html) values (#{articleId}, #{content},#{contentHtml})")
    void insert(ArticleBody articleBody);
}
