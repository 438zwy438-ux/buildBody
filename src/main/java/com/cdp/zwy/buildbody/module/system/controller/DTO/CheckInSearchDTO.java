package com.cdp.zwy.buildbody.module.system.controller.DTO;

import lombok.Data;

/**
 * @author zwy
 * @version 1.0
 * @description: CheckInSearchDTO
 * @date 2026/2/19 09:56
 */
@Data
public class CheckInSearchDTO {
    private String phone; // 手机号（支持后4位或全号）
}