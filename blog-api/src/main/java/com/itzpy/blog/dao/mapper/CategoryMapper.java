package com.itzpy.blog.dao.mapper;

import com.itzpy.blog.vo.CategoryVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CategoryMapper {
    /**
     * 根据分类id查询分类信息
     * @param categoryId 分类id
     * @return 分类信息
     */
    @Select("select id, avatar, category_name, description from category where id = #{categoryId}")
    CategoryVo findCategoryById(Long categoryId);
}
