package com.itzpy.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itzpy.blog.dao.dos.Archives;
import com.itzpy.blog.dao.pojo.Article;
import com.itzpy.blog.vo.HotArticleVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {


    List<Archives> listArchives();


    IPage<Article> listArticle(Page<Article> page,
                               Long categoryId,
                               Long tagId,
                               String year,
                               String month);

    @Select("select id, title from article order by view_counts desc limit #{hotArticleNum}")
    List<HotArticleVo> getHotArticleByViewCount(int hotArticleNum);
}
