package com.cdp.zwy.buildbody.module.business.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cdp.zwy.buildbody.module.business.dao.TbCourseDao;
import com.cdp.zwy.buildbody.module.business.entity.TbCourse;
import com.cdp.zwy.buildbody.module.business.service.TbCourseService;
import org.springframework.stereotype.Service;

/**
 * 课程信息表(TbCourse)表服务实现类
 *
 * @author makejava
 * @since 2026-02-16 09:48:13
 */
@Service("tbCourseService")
public class TbCourseServiceImpl extends ServiceImpl<TbCourseDao, TbCourse> implements TbCourseService {

}

