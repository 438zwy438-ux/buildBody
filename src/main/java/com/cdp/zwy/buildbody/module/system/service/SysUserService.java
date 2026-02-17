package com.cdp.zwy.buildbody.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cdp.zwy.buildbody.module.system.controller.DTO.LoginDTO;
import com.cdp.zwy.buildbody.module.system.controller.DTO.RegisterDTO;
import com.cdp.zwy.buildbody.module.system.controller.VO.LoginVO;
import com.cdp.zwy.buildbody.module.system.entity.SysUser;

/**
 * 系统用户表(SysUser)表服务接口
 *
 * @author makejava
 * @since 2026-02-15 09:14:42
 */
public interface SysUserService extends IService<SysUser> {
    // 增加这个方法定义
    LoginVO login(LoginDTO loginDTO);
    public Boolean registerMember(RegisterDTO dto);


}

