package com.itzpy.blog.service;

import com.itzpy.blog.dao.mapper.ArticleMapper;
import com.itzpy.blog.dao.pojo.Article;
import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ThreadService {

    /**
     * 更新文章的阅读数
     * @param articleMapper 文章的mapper
     * @param article 文章
     */
    @Async("taskExecutor")
    public void updateArticleViewCount(ArticleMapper articleMapper, Article article) {
        int viewCounts = article.getViewCounts();
        Article articleUpdate = new Article();
        BeanUtils.copyProperties(article, articleUpdate);

        articleUpdate.setViewCounts(viewCounts + 1);
        // 判断阅读数是否一致，一致时再进行更新操作，确保线程安全
        if(viewCounts == articleMapper.getById(article.getId()).getViewCounts() ) {
            int i = articleMapper.update(articleUpdate);
            System.out.println("更新文章阅读数，影响行数：" + i);
        }

        try{
            Thread.sleep(1000);
            System.out.println("更新完成了...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}