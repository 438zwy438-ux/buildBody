package com.cdp.zwy.buildbody.module.system.controller.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zwy
 * @version 1.0
 * @description: MemberCheckVO
 * @date 2026/2/19 09:56
 */
@Data
@Schema(description = "会员核验视图对象")
public class MemberCheckVO {
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "真实姓名")
    private String realName;
    
    @Schema(description = "手机号")
    private String phone;
    
    @Schema(description = "会员人像照片URL")
    private String faceImgUrl;
    
    @Schema(description = "账户余额")
    private Double balance;

    // --- 卡状态信息 ---
    @Schema(description = "会员卡名称")
    private String cardName;
    
    @Schema(description = "卡状态描述")
    private String cardStatusStr;
    
    @Schema(description = "是否允许入场")
    private boolean canEntry;
    
    @Schema(description = "过期时间")
    private LocalDateTime expireTime;
    
    @Schema(description = "剩余次数（次卡）")
    private Integer remainCount;
}