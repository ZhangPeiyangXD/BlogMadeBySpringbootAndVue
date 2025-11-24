package com.itzpy.blog.service;

import com.itzpy.blog.dao.pojo.Result;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface UploadService {

    Result upload(MultipartFile file);
}
