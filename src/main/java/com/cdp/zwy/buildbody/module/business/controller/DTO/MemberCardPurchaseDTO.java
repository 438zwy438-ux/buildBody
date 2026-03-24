package com.cdp.zwy.buildbody.module.business.controller.DTO;

import lombok.Data;

/**
 * @author zwy
 * @version 1.0
 * @description: MemberCardPurchaseDTO
 * @date 2026/3/24
 */
@Data
public class MemberCardPurchaseDTO {
    private Long cardTemplateId;  // 会员卡模板ID
    private Long userId;          // 用户ID
    private Integer quantity;       // 购买数量
}