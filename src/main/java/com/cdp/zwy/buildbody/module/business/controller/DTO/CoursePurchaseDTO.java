package com.cdp.zwy.buildbody.module.business.controller.DTO;

import lombok.Data;

/**
 * @author zwy
 * @version 1.0
 * @description: CoursePurchaseDTO
 * @date 2026/2/21
 */
@Data
public class CoursePurchaseDTO {
    private Long courseId;      // 课程ID
    private Long userId;        // 用户ID
    private Integer quantity;    // 购买数量（购买多少个课程产品）
}