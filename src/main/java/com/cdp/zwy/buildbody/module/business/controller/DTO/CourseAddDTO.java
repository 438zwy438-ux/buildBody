package com.cdp.zwy.buildbody.module.business.controller.DTO;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zwy
 * @version 1.0
 * @description: CourseAddDTO
 * @date 2026/2/20 22:12
 */
@Data
public class CourseAddDTO {
    private Long coachUserId;   // 绑定的教练ID (对应 sys_user 表的 ID)
    private String name;        // 课程名称
    private BigDecimal price;   // 价格
    private Integer duration;   // 时长(分钟)
    private String description; // 描述
    private String coverImg;    // 封面图
}