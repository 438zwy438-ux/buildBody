package com.cdp.zwy.buildbody.module.business.controller.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zwy
 * @version 1.0
 * @description: 会员详情视图对象
 * @date 2026/2/26
 */
@Data
@Schema(description = "会员详情视图对象")
public class MemberDetailVO {
    @Schema(description = "档案ID")
    private Long id;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "真实姓名")
    private String realName;
    
    @Schema(description = "性别（0男 1女 2未知）")
    private Integer gender;
    
    @Schema(description = "年龄")
    private Integer age;
    
    @Schema(description = "会员人像照片URL")
    private String faceImgUrl;
    
    @Schema(description = "账户余额")
    private Double balance;
    
    @Schema(description = "积分")
    private Integer points;
    
    @Schema(description = "VIP状态（0普通 1VIP）")
    private Integer isVip;
    
    @Schema(description = "VIP过期时间")
    private LocalDateTime vipExpireTime;
    
    @Schema(description = "出生日期")
    private LocalDateTime birthDate;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    // 用户基础信息
    @Schema(description = "用户名")
    private String username;
    
    @Schema(description = "昵称")
    private String nickname;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "头像URL")
    private String avatar;
}