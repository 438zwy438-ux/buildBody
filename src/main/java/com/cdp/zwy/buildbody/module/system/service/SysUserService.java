package com.cdp.zwy.buildbody.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cdp.zwy.buildbody.module.system.controller.DTO.CoachAddDTO;
import com.cdp.zwy.buildbody.module.system.controller.DTO.LoginDTO;
import com.cdp.zwy.buildbody.module.system.controller.DTO.RegisterDTO;
import com.cdp.zwy.buildbody.module.system.controller.VO.LoginVO;
import com.cdp.zwy.buildbody.module.system.entity.SysUser;

import java.util.List;

public interface SysUserService extends IService<SysUser> {
    LoginVO login(LoginDTO loginDTO);
    Boolean registerMember(RegisterDTO dto);
    Boolean addCoach(CoachAddDTO dto);
    
    List<String> getUserRoles(Long userId);
    Boolean isVip(Long userId);
}