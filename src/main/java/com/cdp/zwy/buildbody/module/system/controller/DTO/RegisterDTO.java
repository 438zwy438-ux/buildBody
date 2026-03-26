package com.cdp.zwy.buildbody.module.system.controller.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zwy
 * @version 1.0
 * @description: RegisterDTO
 * @date 2026/2/17 10:02
 */
@Data
@Schema(description = "用户注册数据传输对象")
public class RegisterDTO implements Serializable {
    // --- 账号基础信息 ---
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "密码")
    private String password;
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "手机号")
    private String phone;

    // --- 档案信息 ---
    @Schema(description = "真实姓名")
    private String realName;
    
    @Schema(description = "性别（0男 1女）")
    private Integer gender;
    
    @Schema(description = "年龄")
    private Integer age;
    
    @Schema(description = "会员人像照片URL")
    private String faceImgUrl;

    // --- 办卡信息 ---
    @Schema(description = "选择的会员卡模板ID")
    private Long cardTemplateId;
}