package com.itzpy.blog.service;

import com.itzpy.blog.dao.pojo.Result;
import com.itzpy.blog.vo.TagVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TagService {
    /**
     * 根据文章id查询标签列表
     * @param articleId 文章id
     * @return List<TagVo> 标签列表
     */
    List<TagVo> findTagsByArticleId(Long articleId);


    /**
     * 查询最热的标签
     * @param limitHotNum 最热的标签的数量
     * @return Result 最热的标签列表
     */
    Result hot(int limitHotNum);


    /**
     * 查询所有标签
     * @return Result 所有标签列表
     */
    Result findAll();


    /**
     * 查询所有标签详情
     * @return Result 所有标签详情列表
     */
    Result findAllDetail();


    /**
     * 根据id查询标签详情
     * @param id 标签id
     * @return Result 标签详情
     */
    Result findDetailById(Long id);
}
