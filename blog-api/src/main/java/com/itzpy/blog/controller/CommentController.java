package com.itzpy.blog.controller;

import com.itzpy.blog.aop.LogAnnotation;
import com.itzpy.blog.dao.pojo.Result;
import com.itzpy.blog.service.CommentService;
import com.itzpy.blog.vo.params.CommentParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    /**
     * 查询文章的评论列表
     * @param id 文章id
     * @return result(评论列表)
     */
    @GetMapping("/article/{id}")
    @LogAnnotation(module = "评论", operator = "查询文章的评论列表")
    public Result comments(@PathVariable("id") Long id) {
        return commentService.commentsByArticleId(id);
    }


    /**
     * 添加评论
     * @param commentParam 评论
     * @return result(添加结果)
     */
    @PostMapping("/create/change")
    @LogAnnotation(module = "评论", operator = "添加评论")
    public Result create(@RequestBody CommentParam commentParam) {
        return commentService.create(commentParam);
    }
}