package com.itzpy.blog.service.impl;

import com.itzpy.blog.dao.mapper.ArticleMapper;
import com.itzpy.blog.dao.mapper.CommentMapper;
import com.itzpy.blog.dao.pojo.Comment;
import com.itzpy.blog.dao.pojo.Result;
import com.itzpy.blog.service.CommentService;
import com.itzpy.blog.service.SysUserService;
import com.itzpy.blog.vo.CommentVo;
import com.itzpy.blog.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private SysUserService sysUserService;


    /**
     * 根据文章id查询评论列表
     * @param id 文章id
     * @return result(评论列表)
     */
    @Override
    public Result commentsByArticleId(Long id) {
        // 查询文章的一级评论 (level = 1)
        List<Comment> parentComments = commentMapper.getCommentsByArticleIdAndLevel(id, 1);
        // 查询文章的二级评论 (level = 2)
        List<Comment> childComments = commentMapper.getCommentsByArticleIdAndLevel(id, 2);

        // 构建评论树
        List<CommentVo> commentVoList = buildCommentTree(parentComments, childComments);

        return Result.success(commentVoList);
    }

    /**
     * 构建评论树结构
     * @param parentComments 一级评论列表
     * @param childComments 二级评论列表
     * @return 组装好的评论树
     */
    private List<CommentVo> buildCommentTree(List<Comment> parentComments, List<Comment> childComments) {
        // 确保列表不为null
        if (parentComments == null) parentComments = new ArrayList<>();
        if (childComments == null) childComments = new ArrayList<>();

        // 将所有一级评论转换为CommentVo
        List<CommentVo> parentCommentVos = new ArrayList<>();
        for (Comment comment : parentComments) {
            parentCommentVos.add(copy(comment));
        }

        // 将二级评论按parent_id分组，便于快速查找
        Map<Long, List<CommentVo>> childCommentMap = new HashMap<>();
        for (Comment child : childComments) {
            CommentVo childVo = copy(child);
            Long parentId = child.getParentId();
            childCommentMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(childVo);
        }

        // 将二级评论分配给对应的一级评论
        for (CommentVo parentVo : parentCommentVos) {
            Long parentId = Long.valueOf(parentVo.getId()); // 将String类型的id转回Long
            List<CommentVo> children = childCommentMap.getOrDefault(parentId, new ArrayList<>());
            parentVo.setChildrens(children);
        }

        return parentCommentVos;
    }


    private CommentVo copy(Comment comment) {
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(comment,commentVo);
        commentVo.setId(String.valueOf(comment.getId())); // Long转String

        //作者信息
        Long authorId = comment.getAuthorId();
        UserVo userVo = this.sysUserService.findUserVoById(authorId);
        commentVo.setAuthor(userVo);

        //to User 给谁评论 (仅对二级评论有效)
        Integer level = comment.getLevel();
        if (level > 1){
            Long toUid = comment.getToUid();
            UserVo toUserVo = this.sysUserService.findUserVoById(toUid);
            commentVo.setToUser(toUserVo);
        }

        // 确保childrens不为null
        if (commentVo.getChildrens() == null) {
            commentVo.setChildrens(new ArrayList<>());
        }

        return commentVo;
    }
}