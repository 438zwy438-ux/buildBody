package com.cdp.zwy.buildbody.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cdp.zwy.buildbody.module.system.entity.SysOrder;

import java.math.BigDecimal;

/**
 * 系统订单表(SysOrder)表服务接口
 *
 * @author makejava
 * @since 2026-02-20 23:03:03
 */
public interface SysOrderService extends IService<SysOrder> {

    /**
     * 创建课程订单
     * @param userId 用户ID
     * @param courseTimes 课程次数
     * @param amount 订单金额
     * @return 订单ID
     */
    Long createCourseOrder(Long userId, Integer courseTimes, BigDecimal amount);
    
    /**
     * 创建会员卡订单
     * @param userId 用户ID
     * @param cardTimes 会员卡次数
     * @param amount 订单金额
     * @return 订单ID
     */
    Long createMemberCardOrder(Long userId, Integer cardTimes, BigDecimal amount);
    
    /**
     * 支付订单
     * @param orderId 订单ID
     * @return 支付结果
     */
    Boolean payOrder(Long orderId);

}