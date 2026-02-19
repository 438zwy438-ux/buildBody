package com.cdp.zwy.buildbody.module.system.controller.VO;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zwy
 * @version 1.0
 * @description: MemberCheckVO
 * @date 2026/2/19 09:56
 */
@Data
public class MemberCheckVO {
    private Long userId;
    private String realName;
    private String phone;
    private String faceImgUrl; // 核心：用于管理员人工比对
    private Double balance;    // 余额

    // --- 卡状态信息 ---
    private String cardName;      // 例如：季卡
    private String cardStatusStr; // "正常", "已过期", "次数不足"
    private boolean canEntry;     // 系统判定是否允许入场
    private LocalDateTime expireTime;      // 过期时间
    private Integer remainCount;  // 剩余次数（如果是次卡）
}