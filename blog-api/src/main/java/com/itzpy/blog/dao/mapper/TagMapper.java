package com.itzpy.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itzpy.blog.dao.pojo.Tag;
import com.itzpy.blog.vo.TagVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 根据文章id查询标签列表
     * @param articleId 文章id
     * @return List<Tag> 标签列表
     */
    List<Tag> findTagsByArticleId(Long articleId);


    /**
     * 根据标签id查询标签列表
     * @param limitHotNum 最热的标签的数量
     * @return List<Long> 标签id列表
     */
    @Select("select tag_id from article_tag group by tag_id order by count(*) desc limit #{limitHotNum}")
    List<Long> findHotTagIds(int limitHotNum);


    /**
     * 根据标签id列表查询标签列表
     * @param tagIds 标签id列表
     * @return List<TagVo> 标签列表
     */
    List<TagVo> findTagByIds(List<Long> tagIds);
}
