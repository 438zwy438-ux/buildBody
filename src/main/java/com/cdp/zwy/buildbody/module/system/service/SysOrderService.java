package com.cdp.zwy.buildbody.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cdp.zwy.buildbody.module.system.controller.VO.CourseOrderVO;
import com.cdp.zwy.buildbody.module.system.entity.SysOrder;

import java.util.List;

/**
 * 系统订单表(SysOrder)表服务接口
 *
 * @author makejava
 * @since 2026-02-21 13:05:15
 */
public interface SysOrderService extends IService<SysOrder> {

    /**
     * 创建会员卡订单
     *
     * @param userId 用户ID
     * @param cardTimes 卡次数
     * @param amount 金额
     * @return 订单ID
     */
    Long createMemberCardOrder(Long userId, Integer cardTimes, Double amount);

    /**
     * 创建课程订单
     *
     * @param userId 用户ID
     * @param courseId 课程ID
     * @param courseTimes 课程次数
     * @param amount 金额
     * @return 订单ID
     */
    Long createCourseOrder(Long userId, Long courseId, Integer courseTimes, Double amount);

    /**
     * 支付订单
     *
     * @param orderId 订单ID
     * @return 支付结果
     */
    Boolean payOrder(Long orderId);

    /**
     * 取消订单（未支付的订单）
     *
     * @param orderId 订单ID
     * @return 取消结果
     */
    Boolean cancelOrder(Long orderId);

    /**
     * 退款订单（已支付的订单）
     *
     * @param orderId 订单ID
     * @return 退款结果
     */
    Boolean refundOrder(Long orderId);

    /**
     * 查询我的私教课订单（多表查询）
     *
     * @param userId 用户ID
     * @return 私教课订单列表
     */
    List<CourseOrderVO> getMyCourseOrders(Long userId);
}