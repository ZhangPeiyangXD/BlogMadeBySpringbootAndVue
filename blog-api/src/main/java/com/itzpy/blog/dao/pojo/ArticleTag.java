package com.itzpy.blog.dao.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class ArticleTag {
    @TableId(type = IdType.AUTO) // 使用数据库自增ID
    private Long id;
    private Long articleId;
    private Long tagId;
}
