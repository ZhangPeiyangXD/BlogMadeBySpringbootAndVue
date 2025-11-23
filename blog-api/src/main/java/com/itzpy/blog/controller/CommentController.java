package com.itzpy.blog.controller;

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
    public Result comments(@PathVariable("id") Long id) {
        log.info("评论列表展示：文章id：{}", id);

        return commentService.commentsByArticleId(id);
    }


    /**
     * 添加评论
     * @param commentParam 评论
     * @return result(添加结果)
     */
    @PostMapping("/create/change")
    public Result create(@RequestBody CommentParam commentParam) {
        log.info("添加评论：{}", commentParam);

        return commentService.create(commentParam);
    }
}