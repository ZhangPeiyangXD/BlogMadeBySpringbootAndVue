package com.itzpy.blog.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.itzpy.blog.admin.pojo.Admin;
import com.itzpy.blog.admin.pojo.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    @Select("SELECT * FROM permission where id in (select permission_id from admin_permission where admin_id=#{adminId})")
    List<Permission> findPermissionByAdminId(Long adminId);
}
