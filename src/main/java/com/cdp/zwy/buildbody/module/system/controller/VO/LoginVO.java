package com.cdp.zwy.buildbody.module.system.controller.VO;

import lombok.Data;
import java.util.List;

@Data
public class LoginVO {
    private Long userId;
    private String nickname;
    private String token;
    private List<String> roles; // 支持多角色: ["admin", "user", "coach", "vip"]
    private String avatar;
}