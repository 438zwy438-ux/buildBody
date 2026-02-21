package com.cdp.zwy.buildbody.module.business.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cdp.zwy.buildbody.module.business.controller.DTO.CourseAddDTO;
import com.cdp.zwy.buildbody.module.business.dao.TbCoachProfileDao;
import com.cdp.zwy.buildbody.module.business.dao.TbCourseDao;
import com.cdp.zwy.buildbody.module.business.entity.TbCoachProfile;
import com.cdp.zwy.buildbody.module.business.entity.TbCourse;
import com.cdp.zwy.buildbody.module.business.service.TbCourseService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 课程信息表(TbCourse)表服务实现类
 *
 * @author makejava
 * @since 2026-02-16 09:48:13
 */
@Service("tbCourseService")
public class TbCourseServiceImpl extends ServiceImpl<TbCourseDao, TbCourse> implements TbCourseService {
    @Resource
    private TbCoachProfileDao coachProfileDao;

    @Override
    public Boolean addPrivateCourse(CourseAddDTO dto) {
        // 1. 严谨性校验：确认该教练是否存在
        TbCoachProfile coach = coachProfileDao.selectOne(new QueryWrapper<TbCoachProfile>()
                .eq("user_id", dto.getCoachUserId()));
        if (coach == null) {
            throw new RuntimeException("绑定的教练不存在，无法发布课程！");
        }

        // 2. 创建课程实体
        TbCourse course = new TbCourse();
        course.setCoachUserId(dto.getCoachUserId());
        course.setName(dto.getName());
        course.setType(dto.getType() != null ? dto.getType() : 1); // 默认为私教课
        course.setPrice(dto.getPrice());
        course.setCourseTimes(dto.getCourseTimes());
        course.setDuration(dto.getDuration());
        course.setDescription(dto.getDescription());
        course.setCoverImg(dto.getCoverImg());
        course.setStatus(1); // 1默认上架
        course.setCreateTime(new Date()); // 设置创建时间

        this.baseMapper.insert(course);
        return true;
    }

}