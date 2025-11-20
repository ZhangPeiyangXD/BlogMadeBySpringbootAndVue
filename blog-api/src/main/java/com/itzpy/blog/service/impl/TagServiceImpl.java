package com.itzpy.blog.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.itzpy.blog.dao.mapper.TagMapper;
import com.itzpy.blog.dao.pojo.Tag;
import com.itzpy.blog.service.TagService;
import com.itzpy.blog.vo.Result;
import com.itzpy.blog.vo.TagVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {
    @Autowired
    private TagMapper tagMapper;


    /**
     * 根据文章id查询标签列表
     * @param articleId 文章id
     * @return List<TagVo> 标签列表
     */
    @Override
    public List<TagVo> findTagsByArticleId(Long articleId) {
        // article_tag是文章-标签关系表。通过文章id查询文章标签，
        // tag是标签表。再通过标签id查询标签详细信息

        List<Tag> tags = tagMapper.findTagsByArticleId(articleId);

        return copyList(tags);
    }


    /**
     * 查询最热的标签
     * @param limitHotNum 最热的标签的数量
     * @return Result 最热的标签列表
     */
    @Override
    public Result hot(int limitHotNum) {
        // 先查询前limitHotNum个最热的标签id
        List<Long> tagIds = tagMapper.findHotTagIds(limitHotNum);

        // 再根据id列表查询标签详细信息
        if(CollectionUtils.isEmpty(tagIds)){
            return Result.success(Collections.emptyList());
        }

        List<TagVo> tagList = tagMapper.findTagByIds(tagIds);

        return Result.success(tagList);
    }


    /**
     * 拷贝列表
     * @param tags 标签列表
     * @return List<TagVo> 拷贝后的标签列表
     */
    private List<TagVo> copyList(List<Tag> tags) {
        List<TagVo> tagVoList = new ArrayList<>();
        tags.forEach(tag -> tagVoList.add(copy(tag)));

        return tagVoList;
    }


    /**
     * 拷贝
     * @param tag 标签
     * @return TagVo 拷贝后的标签
     */
    private TagVo copy(Tag tag) {
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag, tagVo);
        return tagVo;
    }

}
