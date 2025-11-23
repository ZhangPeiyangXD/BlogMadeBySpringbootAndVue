package com.itzpy.blog.service;

import com.itzpy.blog.dao.pojo.Result;
import org.springframework.stereotype.Service;

@Service
public interface CategoryService {
    Result findAll();
}
