package com.itzpy.blog.controller;

import com.itzpy.blog.service.TagService;
import com.itzpy.blog.dao.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tags")
@Slf4j
public class TagController {
    private static final int HOT_TAG_NUM = 6;
    @Autowired
    private TagService tagService;

    @GetMapping("/hot")
    public Result hot() {
        log.info("查询最热标签");

        return tagService.hot(HOT_TAG_NUM);
    }
}
