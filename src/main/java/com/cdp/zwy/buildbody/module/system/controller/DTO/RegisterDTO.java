package com.cdp.zwy.buildbody.module.system.controller.DTO;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zwy
 * @version 1.0
 * @description: RegisterDTO
 * @date 2026/2/17 10:02
 */
@Data
public class RegisterDTO implements Serializable {
    // --- 账号基础信息 ---
    private String username;
    private String password;
    private String nickname;
    private String phone;

    // --- 档案信息 ---
    private String realName;
    private Integer gender; // 0男 1女
    private Integer age;
    private String faceImgUrl;

    // --- 办卡信息 ---
    private Long cardTemplateId; // 选择了哪种会员卡
}
