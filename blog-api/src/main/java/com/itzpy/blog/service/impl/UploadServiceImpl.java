package com.itzpy.blog.service.impl;

import com.itzpy.blog.dao.pojo.ErrorCode;
import com.itzpy.blog.dao.pojo.Result;
import com.itzpy.blog.service.UploadService;
import com.itzpy.blog.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class UploadServiceImpl implements UploadService {
    @Autowired
    private QiniuUtils qiniuUtils;

    @Override
    public Result upload(MultipartFile file) {
        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        String fileName = null;
        if (originalFilename != null) {
            fileName = UUID.randomUUID().toString() + "."
                    + originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }else{
            Result fail = Result.fail(ErrorCode.PARAMS_ERROR.getCode(), ErrorCode.PARAMS_ERROR.getMsg());
        }

        //上传文件到七牛云,降低自身服务器的负载
        boolean upload = qiniuUtils.upload(file, fileName);
        if(upload){
            return Result.success(QiniuUtils.url + fileName);
        }
        return Result.fail(ErrorCode.UPLOAD_ERROR.getCode(), ErrorCode.UPLOAD_ERROR.getMsg());
    }
}
