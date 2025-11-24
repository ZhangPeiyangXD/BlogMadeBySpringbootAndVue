package com.itzpy.blog.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itzpy.blog.admin.pojo.Permission;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
}
