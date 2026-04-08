package com.cdp.zwy.buildbody.module.system.controller.VO;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户信息VO（包含角色）
 *
 * @author zwy
 * @version 1.0
 * @description: UserVO
 * @date 2026-04-08
 */
@Data
public class UserVO implements Serializable {
    // ====== SysUser 字段 ======
    private Long userId;
    private String username;
    private String nickname;
    private String phone;
    private String avatar;
    private Integer status;
    private Date createTime;
    private Date updateTime;

    // ====== 角色信息 ======
    private List<String> roles;
    private String roleKey; // 主角色key，方便前端显示
}