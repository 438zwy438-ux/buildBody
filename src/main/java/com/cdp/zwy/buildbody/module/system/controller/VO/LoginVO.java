package com.cdp.zwy.buildbody.module.system.controller.VO;

import lombok.Data;

/**
 * @author zwy
 * @version 1.0
 * @description: LoginVO
 * @date 2026/2/17 09:48
 */
@Data
public class LoginVO {
    private Long userId;
    private String nickname;
    private String token;
    private String role; // "ADMIN", "MEMBER", "COACH"
    private String avatar;
}
