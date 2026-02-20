package com.cdp.zwy.buildbody.module.system.controller.DTO;

import lombok.Data;

/**
 * @author zwy
 * @version 1.0
 * @description: CoachAddDTO
 * @date 2026/2/20 22:01
 */
@Data
public class CoachAddDTO {
    // 系统账号信息
    private String username;
    private String password;
    private String phone;

    // 教练档案信息
    private String realName;
    private String specialty; // 特长标签(如: 减脂,增肌)
    private String intro;     // 简介
}