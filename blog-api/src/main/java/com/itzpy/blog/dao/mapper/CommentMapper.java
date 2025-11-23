package com.itzpy.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itzpy.blog.dao.pojo.Comment;
import com.itzpy.blog.vo.CommentVo;
import com.itzpy.blog.vo.UserVo;
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
     * 根据父评论ID查询子评论列表
     * @param parentId 父评论ID
     * @return 子评论列表
     */
    @Select("SELECT * FROM comment WHERE parent_id = #{parentId} ORDER BY create_date ASC")
    List<Comment> getCommentsByParentId(@Param("parentId") Long parentId);
}