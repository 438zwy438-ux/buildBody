package com.cdp.zwy.buildbody.module.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cdp.zwy.buildbody.module.business.entity.TbCourseBooking;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 课程预约记录表(TbCourseBooking)表服务接口
 *
 * @author makejava
 * @since 2026-02-16 09:50:03
 */
public interface TbCourseBookingService extends IService<TbCourseBooking> {

    @Transactional(rollbackFor = Exception.class)
    Long bookCourse(Long userId, Long coachUserId, Long courseId, Date scheduleTime);

    @Transactional(rollbackFor = Exception.class)
    Boolean checkCourse(Long bookingId);
}