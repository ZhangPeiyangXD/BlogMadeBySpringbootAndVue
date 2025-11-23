package com.itzpy.blog.service.impl;

import com.itzpy.blog.dao.mapper.ArticleMapper;
import com.itzpy.blog.dao.mapper.CommentMapper;
import com.itzpy.blog.dao.pojo.Comment;
import com.itzpy.blog.dao.pojo.ErrorCode;
import com.itzpy.blog.dao.pojo.Result;
import com.itzpy.blog.dao.pojo.SysUser;
import com.itzpy.blog.service.CommentService;
import com.itzpy.blog.service.SysUserService;
import com.itzpy.blog.utils.UserThreadLocal;
import com.itzpy.blog.vo.CommentVo;
import com.itzpy.blog.vo.UserVo;
import com.itzpy.blog.vo.params.CommentParam;
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
     * 创建评论
     * @param commentParam 评论参数
     * @return result(评论结果)
     */
    @Override
    public Result create(CommentParam commentParam) {
        // 检查参数
        if (commentParam == null || commentParam.getContent() == null || commentParam.getArticleId() == null) {
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }
        
        // 构造评论对象
        Comment comment = new Comment();
        comment.setArticleId(commentParam.getArticleId());
        comment.setContent(commentParam.getContent());
        comment.setCreateDate(System.currentTimeMillis());
        
        // 设置作者ID，如果用户已登录则使用登录用户ID，否则使用默认匿名用户ID
        SysUser currentUser = UserThreadLocal.get();
        if (currentUser != null) {
            comment.setAuthorId(currentUser.getId());
        } else {
            // 匿名用户发表评论，使用默认作者ID (例如系统管理员ID)
            comment.setAuthorId(1234L);
        }
        
        // 判断是评论文章还是一级评论
        if (commentParam.getParent() == null || commentParam.getParent() == 0) {
            // 评论文章
            comment.setLevel(1);
        } else {
            // 回复评论
            comment.setParentId(commentParam.getParent());
            comment.setToUid(commentParam.getToUserId());
            comment.setLevel(2);
        }
        
        // 插入评论到数据库
        int insertResult = commentMapper.insertComment(comment);
        if (insertResult != 1) {
            return Result.fail(ErrorCode.SYSTEM_ERROR.getCode(), ErrorCode.SYSTEM_ERROR.getMsg());
        }
        
        // 更新文章的评论数
        articleMapper.updateCommentCount(commentParam.getArticleId());
        
        // 转换为CommentVo返回
        CommentVo commentVo = copy(comment);
        return Result.success(commentVo);
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
            // 确保parentId不为null
            if (parentId != null) {
                childCommentMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(childVo);
            }
        }
        
        // 将二级评论分配给对应的一级评论
        for (CommentVo parentVo : parentCommentVos) {
            // 确保parentVo.getId()不为null (CommentVo中的id是Long类型)
            if (parentVo.getId() != null) {
                Long parentId = parentVo.getId(); // 直接使用Long类型的id
                List<CommentVo> children = childCommentMap.getOrDefault(parentId, new ArrayList<>());
                parentVo.setChildrens(children);
            } else {
                // 如果id为null，设置空的children列表
                parentVo.setChildrens(new ArrayList<>());
            }
        }
        
        return parentCommentVos;
    }


    /**
     * 将Comment对象转换为CommentVo对象
     * @param comment Comment对象
     * @return CommentVo对象
     */
    private CommentVo copy(Comment comment) {
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(comment,commentVo);
        // 保持id为Long类型，序列化时会自动转换为String
        commentVo.setId(comment.getId());

        //作者信息
        Long authorId = comment.getAuthorId();
        if (authorId != null) {
            UserVo userVo = this.sysUserService.findUserVoById(authorId);
            commentVo.setAuthor(userVo);
        }

        //to User 给谁评论 (仅对二级评论有效)
        Integer level = comment.getLevel();
        if (level != null && level > 1){
            Long toUid = comment.getToUid();
            if (toUid != null) {
                UserVo toUserVo = this.sysUserService.findUserVoById(toUid);
                commentVo.setToUser(toUserVo);
            }
        }
        
        // 确保childrens不为null
        if (commentVo.getChildrens() == null) {
            commentVo.setChildrens(new ArrayList<>());
        }

        return commentVo;
    }
}