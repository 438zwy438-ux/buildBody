package com.cdp.zwy.buildbody.module.business.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.business.entity.TbCourseBooking;
import com.cdp.zwy.buildbody.module.business.service.TbCourseBookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 课程预约记录表(TbCourseBooking)表控制层
 *
 * @author makejava
 * @since 2026-02-16 09:50:03
 */
@RestController
@Tag(name = "课程预约记录相关接口")
@RequestMapping("/tbCourseBooking")
public class TbCourseBookingController {
    /**
     * 服务对象
     */
    @Resource
    private TbCourseBookingService tbCourseBookingService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param tbCourseBooking 查询实体
     * @return 所有数据
     */
    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    public Result<Page<TbCourseBooking>> selectAll(Page<TbCourseBooking> page, TbCourseBooking tbCourseBooking) {
        return Result.success(this.tbCourseBookingService.page(page, new QueryWrapper<>(tbCourseBooking)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    public Result<TbCourseBooking> selectOne(@PathVariable Serializable id) {
        return Result.success(this.tbCourseBookingService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param tbCourseBooking 实体对象
     * @return 新增结果
     */
    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    public Result<Boolean> insert(@RequestBody TbCourseBooking tbCourseBooking) {
        return Result.success(this.tbCourseBookingService.save(tbCourseBooking));
    }

    /**
     * 修改数据
     *
     * @param tbCourseBooking 实体对象
     * @return 修改结果
     */
    @Operation(summary = "修改数据")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody TbCourseBooking tbCourseBooking) {
        return Result.success(this.tbCourseBookingService.updateById(tbCourseBooking));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @Operation(summary = "删除数据")
    @DeleteMapping("/delete")
    public Result<Boolean> delete(@RequestParam("idList") List<Long> idList) {
        return Result.success(this.tbCourseBookingService.removeByIds(idList));
    }
    
    /**
     * 预约课程
     *
     * @param userId 用户ID
     * @param coachUserId 教练ID
     * @param courseId 课程ID
     * @param scheduleTime 预约上课时间
     * @return 预约ID
     */
    @Operation(summary = "预约课程")
    @PostMapping("/book")
    public Result<Long> bookCourse(@RequestParam Long userId,
                                  @RequestParam Long coachUserId,
                                  @RequestParam Long courseId,
                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date scheduleTime) {
        Long bookingId = tbCourseBookingService.bookCourse(userId, coachUserId, courseId, scheduleTime);
        return Result.success(bookingId);
    }
    
    /**
     * 核销课程
     *
     * @param bookingId 预约ID
     * @return 核销结果
     */
    @Operation(summary = "核销课程")
    @PostMapping("/check")
    public Result<Boolean> checkCourse(@RequestParam Long bookingId) {
        Boolean result = tbCourseBookingService.checkCourse(bookingId);
        return Result.success(result);
    }
    
    /**
     * 查询用户的预约记录
     *
     * @param userId 用户ID
     * @return 预约记录列表
     */
    @Operation(summary = "查询用户的预约记录")
    @GetMapping("/user/{userId}")
    public Result<List<TbCourseBooking>> getUserBookings(@PathVariable Long userId) {
        QueryWrapper<TbCourseBooking> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_time");
        return Result.success(tbCourseBookingService.list(queryWrapper));
    }
    
    /**
     * 查询教练的预约记录
     *
     * @param coachUserId 教练ID
     * @return 预约记录列表
     */
    @Operation(summary = "查询教练的预约记录")
    @GetMapping("/coach/{coachUserId}")
    public Result<List<TbCourseBooking>> getCoachBookings(@PathVariable Long coachUserId) {
        QueryWrapper<TbCourseBooking> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("coach_user_id", coachUserId);
        queryWrapper.orderByDesc("create_time");
        return Result.success(tbCourseBookingService.list(queryWrapper));
    }
}