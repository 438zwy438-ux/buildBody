package com.cdp.zwy.buildbody.module.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cdp.zwy.buildbody.module.system.dao.SysOrderDao;
import com.cdp.zwy.buildbody.module.system.entity.SysOrder;
import com.cdp.zwy.buildbody.module.system.service.SysOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * 系统订单表(SysOrder)表服务实现类
 *
 * @author makejava
 * @since 2026-02-20 23:03:04
 */
@Service("sysOrderService")
public class SysOrderServiceImpl extends ServiceImpl<SysOrderDao, SysOrder> implements SysOrderService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCourseOrder(Long userId, Integer courseTimes, BigDecimal amount) {
        // 1. 生成订单号
        String orderNo = generateOrderNo();
        
        // 2. 创建订单
        SysOrder order = new SysOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setType(2); // 2-课程预约
        order.setAmount(amount);
        order.setPayStatus(0); // 0-待支付
        order.setRemainCount(courseTimes);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        
        this.save(order);
        return order.getId();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMemberCardOrder(Long userId, Integer cardTimes, BigDecimal amount) {
        // 1. 生成订单号
        String orderNo = generateOrderNo();
        
        // 2. 创建订单
        SysOrder order = new SysOrder();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setType(1); // 1-会员卡
        order.setAmount(amount);
        order.setPayStatus(0); // 0-待支付
        order.setRemainCount(cardTimes);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        
        this.save(order);
        return order.getId();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean payOrder(Long orderId) {
        SysOrder order = this.getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (order.getPayStatus() != 0) {
            throw new RuntimeException("订单已支付或已退款");
        }
        
        // 更新订单状态为已支付
        order.setPayStatus(1); // 1-已支付
        order.setPayTime(new Date());
        order.setUpdateTime(new Date());
        
        return this.updateById(order);
    }
    
    /**
     * 生成订单号
     * @return 订单号
     */
    private String generateOrderNo() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStr = sdf.format(new Date());
        Random random = new Random();
        int randomNum = random.nextInt(10000);
        return "ORD" + timeStr + String.format("%04d", randomNum);
    }
}