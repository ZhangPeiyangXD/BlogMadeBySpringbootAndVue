package com.itzpy.blog.controller;

import com.itzpy.blog.dao.pojo.ErrorCode;
import com.itzpy.blog.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.itzpy.blog.dao.pojo.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UploadController {
    @Autowired
    private UploadService uploadService;

    @PostMapping
    public Result upload(@RequestParam("image") MultipartFile file) {
        return uploadService.upload(file);
    }
}
