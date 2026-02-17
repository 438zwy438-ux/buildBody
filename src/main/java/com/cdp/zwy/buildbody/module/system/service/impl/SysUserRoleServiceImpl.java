package com.cdp.zwy.buildbody.module.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cdp.zwy.buildbody.module.system.dao.SysUserRoleDao;
import com.cdp.zwy.buildbody.module.system.entity.SysUserRole;
import com.cdp.zwy.buildbody.module.system.service.SysUserRoleService;
import org.springframework.stereotype.Service;

/**
 * 用户和角色关联表(SysUserRole)表服务实现类
 *
 * @author makejava
 * @since 2026-02-17 10:22:38
 */
@Service("sysUserRoleService")
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleDao, SysUserRole> implements SysUserRoleService {

}

