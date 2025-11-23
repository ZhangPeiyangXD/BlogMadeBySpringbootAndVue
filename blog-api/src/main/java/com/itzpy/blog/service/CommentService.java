package com.itzpy.blog.service;

import com.itzpy.blog.dao.pojo.Result;
import com.itzpy.blog.vo.params.CommentParam;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {
    /**
     * 根据文章id查询评论列表
     * @param id 文章id
     * @return result(评论列表)
     */
    Result commentsByArticleId(Long id);


    Result create(CommentParam commentParam);
}
