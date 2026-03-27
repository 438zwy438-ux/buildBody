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

import java.text.SimpleDateFormat;
import java.util.*;

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
    public Long bookCourse(Long userId, Long courseId, Date scheduleTime) {
        TbCourse course = tbCourseService.getById(courseId);
        if (course == null || course.getType() != 1) {
            throw new RuntimeException("课程不存在或不是私教课");
        }
        
        Long coachUserId = course.getCoachUserId();
        if (coachUserId == null) {
            throw new RuntimeException("课程未关联教练");
        }
        
        List<SysOrder> orders = sysOrderService.list(new QueryWrapper<SysOrder>()
                .eq("user_id", userId)
                .eq("type", 2)
                .eq("status", 1)
                .gt("remain_count", 0));
        
        if (orders.isEmpty()) {
            throw new RuntimeException("您没有可用的私教课次数，请先购买");
        }
        
        TbCoachProfile coach = coachProfileService.getOne(new QueryWrapper<TbCoachProfile>()
                .eq("user_id", coachUserId)
                .eq("status", 1));
        
        if (coach == null) {
            throw new RuntimeException("教练不存在或已离职");
        }
        
        SysOrder availableOrder = orders.get(0);
        
        TbCourseBooking booking = new TbCourseBooking();
        booking.setOrderId(availableOrder.getId());
        booking.setUserId(userId);
        booking.setCoachUserId(coachUserId);
        booking.setCourseId(courseId);
        booking.setScheduleTime(scheduleTime);
        booking.setStatus(0);
        booking.setCreateTime(new Date());
        
        this.save(booking);
        
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean cancelBooking(Long bookingId) {
        // 1. 获取预约记录
        TbCourseBooking booking = this.getById(bookingId);
        if (booking == null) {
            throw new RuntimeException("预约记录不存在");
        }
        
        if (booking.getStatus() != 0) {
            throw new RuntimeException("只能取消待核销的预约");
        }
        
        // 2. 更新预约状态为已取消
        booking.setStatus(2); // 2-已取消
        this.updateById(booking);
        
        // 3. 恢复订单的剩余次数
        SysOrder order = sysOrderService.getById(booking.getOrderId());
        if (order != null) {
            order.setRemainCount(order.getRemainCount() + 1);
            sysOrderService.updateById(order);
        }
        
        return true;
    }

    @Override
    public List<Map<String, Object>> getAvailableSlots(Long coachId) {
        //1. 定义固定的工作时间段
        String[] timeSlots = {
            "10:00-11:00", "11:00-12:00", "13:00-14:00", 
            "14:00-15:00", "15:00-16:00", "16:00-17:00", 
            "17:00-18:00", "20:00-21:00", "21:00-22:00"
        };
        
        //2. 计算未来3天的日期范围
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        Date startDate = calendar.getTime();
        
        calendar.add(Calendar.DAY_OF_MONTH, 3);
        Date endDate = calendar.getTime();
        
        //3. 查询数据库中该教练在这3天内的所有预约记录（包括待核销和已完成的）
        QueryWrapper<TbCourseBooking> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("coach_user_id", coachId);
        queryWrapper.ge("schedule_time", startDate);
        queryWrapper.lt("schedule_time", endDate);
        queryWrapper.in("status", Arrays.asList(0, 1)); // 0-待核销 1-已完成
        
        List<TbCourseBooking> bookedSlots = this.list(queryWrapper);
        
        //4. 获取当前时间
        Date now = new Date();
        
        //5. 构建返回结果
        List<Map<String, Object>> result = new ArrayList<>();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        
        for (int i = 0; i < 3; i++) {
            Calendar dayCalendar = Calendar.getInstance();
            dayCalendar.setTime(startDate);
            dayCalendar.add(Calendar.DAY_OF_MONTH, i);
            Date currentDate = dayCalendar.getTime();
            
            Map<String, Object> dayMap = new HashMap<>();
            dayMap.put("date", dateFormat.format(currentDate));
            
            List<Map<String, Object>> slots = new ArrayList<>();
            for (String timeSlot : timeSlots) {
                String[] parts = timeSlot.split("-");
                String startTimeStr = parts[0];
                String endTimeStr = parts[1];
                
                // 解析开始时间和结束时间
                Date startTime = parseTime(currentDate, startTimeStr);
                Date endTime = parseTime(currentDate, endTimeStr);
                
                // 检查这个时间段是否已被预约
                boolean isBooked = false;
                for (TbCourseBooking booking : bookedSlots) {
                    Date bookingTime = booking.getScheduleTime();
                    
                    // 判断预约时间是否在这个时间段内
                    if (bookingTime.compareTo(startTime) >= 0 && bookingTime.compareTo(endTime) < 0) {
                        isBooked = true;
                        break;
                    }
                }
                
                // 对于当天的时间段，检查是否已经过了当前时间
                if (i == 0 && startTime.before(now)) {
                    isBooked = true;
                }
                
                Map<String, Object> slotMap = new HashMap<>();
                slotMap.put("timeSlot", timeSlot);
                slotMap.put("startTime", startTime);
                slotMap.put("endTime", endTime);
                slotMap.put("isBooked", isBooked);
                
                slots.add(slotMap);
            }
            
            dayMap.put("slots", slots);
            result.add(dayMap);
        }
        
        return result;
    }
    
    private Date parseTime(Date baseDate, String timeStr) {
        String[] parts = timeStr.split(":");
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(baseDate);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        return cal.getTime();
    }

}