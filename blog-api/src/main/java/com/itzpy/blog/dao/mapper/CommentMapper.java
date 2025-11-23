package com.itzpy.blog.dao.mapper;

import com.itzpy.blog.dao.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper {
    /**
     * 根据文章ID和评论等级查询评论列表
     * @param articleId 文章ID
     * @param level 评论等级
     * @return 评论列表
     */
    @Select("SELECT * FROM comment WHERE article_id = #{articleId} AND level = #{level} ORDER BY create_date ASC")
    List<Comment> getCommentsByArticleIdAndLevel(@Param("articleId") Long articleId, @Param("level") Integer level);

    /**
     * 插入评论
     * @param comment 评论对象
     * @return 影响行数
     */
    int insertComment(Comment comment);
}