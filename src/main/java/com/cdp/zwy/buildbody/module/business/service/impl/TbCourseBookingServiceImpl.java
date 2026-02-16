package com.cdp.zwy.buildbody.module.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cdp.zwy.buildbody.module.business.dao.TbCourseBookingDao;
import com.cdp.zwy.buildbody.module.business.entity.TbCourseBooking;
import com.cdp.zwy.buildbody.module.business.service.TbCourseBookingService;
import org.springframework.stereotype.Service;

/**
 * 课程预约记录表(TbCourseBooking)表服务实现类
 *
 * @author makejava
 * @since 2026-02-16 09:50:03
 */
@Service("tbCourseBookingService")
public class TbCourseBookingServiceImpl extends ServiceImpl<TbCourseBookingDao, TbCourseBooking> implements TbCourseBookingService {

}

