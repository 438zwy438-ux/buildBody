package com.cdp.zwy.buildbody.module.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cdp.zwy.buildbody.module.business.dao.TbCourseBookingDao;
import com.cdp.zwy.buildbody.module.business.entity.TbCourseBooking;
import com.cdp.zwy.buildbody.module.business.entity.TbCourse;
import com.cdp.zwy.buildbody.module.business.entity.TbCoachProfile;
import com.cdp.zwy.buildbody.module.business.entity.TbMemberProfile;
import com.cdp.zwy.buildbody.module.business.service.TbCourseBookingService;
import com.cdp.zwy.buildbody.module.business.service.TbCourseService;
import com.cdp.zwy.buildbody.module.business.service.TbCoachProfileService;
import com.cdp.zwy.buildbody.module.business.service.TbMemberProfileService;
import com.cdp.zwy.buildbody.module.system.entity.SysOrder;
import com.cdp.zwy.buildbody.module.system.service.SysOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 课程预约记录表(TbCourseBooking)表服务实现类
 *
 * @author makejava
 * @since 2026-02-16 09:50:03
 */
@Service("tbCourseBookingService")
public class TbCourseBookingServiceImpl extends ServiceImpl<TbCourseBookingDao, TbCourseBooking> implements TbCourseBookingService {

    @Resource
    private SysOrderService sysOrderService;
    
    @Resource
    private TbMemberProfileService memberProfileService;
    
    @Resource
    private TbCourseService tbCourseService;
    
    @Resource
    private TbCoachProfileService coachProfileService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long bookCourse(Long userId, Long coachUserId, Long courseId, Date scheduleTime) {
        // 1. 检查用户是否有可用的私教课次数
        List<SysOrder> orders = sysOrderService.list(new QueryWrapper<SysOrder>()
                .eq("user_id", userId)
                .eq("type", 2) // 2-私教课
                .eq("status", 1) // 1-已支付
                .gt("remain_count", 0)); // 剩余次数大于0
        
        if (orders.isEmpty()) {
            throw new RuntimeException("您没有可用的私教课次数，请先购买");
        }
        
        // 2. 检查课程是否存在且为私教课
        TbCourse course = tbCourseService.getById(courseId);
        if (course == null || course.getType() != 1) { // 1-私教
            throw new RuntimeException("课程不存在或不是私教课");
        }
        
        // 3. 检查教练是否存在且在职
        TbCoachProfile coach = coachProfileService.getOne(new QueryWrapper<TbCoachProfile>()
                .eq("user_id", coachUserId)
                .eq("status", 1)); // 1-在职
        
        if (coach == null) {
            throw new RuntimeException("教练不存在或已离职");
        }
        
        // 4. 获取第一个可用的订单
        SysOrder availableOrder = orders.get(0);
        
        // 5. 创建预约记录
        TbCourseBooking booking = new TbCourseBooking();
        booking.setOrderId(availableOrder.getId());
        booking.setUserId(userId);
        booking.setCoachUserId(coachUserId);
        booking.setCourseId(courseId);
        booking.setScheduleTime(scheduleTime);
        booking.setStatus(0); // 0-待核销
        booking.setCreateTime(new Date());
        
        this.save(booking);
        
        // 6. 扣减订单中的剩余次数
        availableOrder.setRemainCount(availableOrder.getRemainCount() - 1);
        sysOrderService.updateById(availableOrder);
        
        return booking.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean checkCourse(Long bookingId) {
        // 1. 获取预约记录
        TbCourseBooking booking = this.getById(bookingId);
        if (booking == null) {
            throw new RuntimeException("预约记录不存在");
        }
        
        if (booking.getStatus() != 0) {
            throw new RuntimeException("该预约已核销或已取消");
        }
        
        // 2. 更新预约状态为已完成
        booking.setStatus(1); // 1-已完成
        booking.setCheckTime(new Date());
        this.updateById(booking);
        
        // 3. 检查用户是否还有剩余的私教课次数
        List<SysOrder> orders = sysOrderService.list(new QueryWrapper<SysOrder>()
                .eq("user_id", booking.getUserId())
                .eq("type", 2) // 2-私教课
                .eq("status", 1) // 1-已支付
                .gt("remain_count", 0)); // 剩余次数大于0
        
        // 4. 如果没有剩余次数，将用户降级为普通会员
        if (orders.isEmpty()) {
            TbMemberProfile memberProfile = memberProfileService.getOne(
                    new QueryWrapper<TbMemberProfile>().eq("user_id", booking.getUserId()));
            if (memberProfile != null && memberProfile.getIsVip() == 1) {
                memberProfile.setIsVip(0); // 0-普通会员
                memberProfileService.updateById(memberProfile);
            }
        }
        
        return true;
    }
}