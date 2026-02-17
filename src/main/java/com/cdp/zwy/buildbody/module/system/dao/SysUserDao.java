package com.cdp.zwy.buildbody.module.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cdp.zwy.buildbody.module.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户表(SysUser)表数据库访问层
 *
 * @author makejava
 * @since 2026-02-15 09:14:42
 */
@Mapper
public interface SysUserDao extends BaseMapper<SysUser> {


}

