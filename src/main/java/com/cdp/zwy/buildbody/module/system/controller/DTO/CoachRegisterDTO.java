package com.cdp.zwy.buildbody.module.system.controller.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zwy
 * @version 1.0
 * @description: CoachRegisterDTO
 * @date 2026/4/6
 */
@Data
@Schema(description = "教练注册数据传输对象")
public class CoachRegisterDTO implements Serializable {
    
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "密码")
    private String password;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "真实姓名")
    private String realName;
    
    @Schema(description = "特长标签(如: 减脂,增肌)")
    private String specialty;
    
    @Schema(description = "个人简介")
    private String intro;
    
    @Schema(description = "证书图片URL列表")
    private List<String> certificates;
    
    @Schema(description = "教练美照URL列表")
    private List<String> images;
}