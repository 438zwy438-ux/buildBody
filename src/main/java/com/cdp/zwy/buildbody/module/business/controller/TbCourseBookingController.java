package com.cdp.zwy.buildbody.module.business.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.annotation.RequireRole;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.business.entity.TbCourseBooking;
import com.cdp.zwy.buildbody.module.business.service.TbCourseBookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "课程预约记录相关接口")
@RequestMapping("/tbCourseBooking")
public class TbCourseBookingController {
    @Resource
    private TbCourseBookingService tbCourseBookingService;

    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    @RequireRole("admin")
    public Result<Page<TbCourseBooking>> selectAll(Page<TbCourseBooking> page, TbCourseBooking tbCourseBooking) {
        return Result.success(this.tbCourseBookingService.page(page, new QueryWrapper<>(tbCourseBooking)));
    }

    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    @RequireRole("admin")
    public Result<TbCourseBooking> selectOne(@PathVariable Serializable id) {
        return Result.success(this.tbCourseBookingService.getById(id));
    }

    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    @RequireRole("admin")
    public Result<Boolean> insert(@RequestBody TbCourseBooking tbCourseBooking) {
        return Result.success(this.tbCourseBookingService.save(tbCourseBooking));
    }

    @Operation(summary = "修改数据")
    @PutMapping("/update")
    @RequireRole("admin")
    public Result<Boolean> update(@RequestBody TbCourseBooking tbCourseBooking) {
        return Result.success(this.tbCourseBookingService.updateById(tbCourseBooking));
    }

    @Operation(summary = "删除数据")
    @DeleteMapping("/delete")
    @RequireRole("admin")
    public Result<Boolean> delete(@RequestParam("idList") List<Long> idList) {
        return Result.success(this.tbCourseBookingService.removeByIds(idList));
    }
    
    @Operation(summary = "预约课程")
    @PostMapping("/book")
    @RequireRole("vip")
    public Result<Long> bookCourse(@RequestParam Long courseId,
                                  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date scheduleTime,
                                  HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Long bookingId = tbCourseBookingService.bookCourse(userId, courseId, scheduleTime);
        return Result.success(bookingId);
    }
    
    @Operation(summary = "核销课程")
    @PostMapping("/check")
    @RequireRole({"admin", "coach"})
    public Result<Boolean> checkCourse(@RequestParam Long bookingId, HttpServletRequest request) {
        List<String> roles = (List<String>) request.getAttribute("roles");
        Long userId = (Long) request.getAttribute("userId");
        
        TbCourseBooking booking = tbCourseBookingService.getById(bookingId);
        if (booking == null) {
            return Result.error("预约记录不存在");
        }
        
        if (roles.contains("coach") && !booking.getCoachUserId().equals(userId)) {
            return Result.error("只能核销自己的课程");
        }
        
        Boolean result = tbCourseBookingService.checkCourse(bookingId);
        return Result.success(result);
    }
    
    @Operation(summary = "查询我的预约记录")
    @GetMapping("/my-bookings")
    @RequireRole({"user", "vip"})
    public Result<List<TbCourseBooking>> getMyBookings(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        QueryWrapper<TbCourseBooking> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_time");
        return Result.success(tbCourseBookingService.list(queryWrapper));
    }
    
    @Operation(summary = "查询教练的预约记录")
    @GetMapping("/coach-bookings")
    @RequireRole({"admin", "coach"})
    public Result<List<TbCourseBooking>> getCoachBookings(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<String> roles = (List<String>) request.getAttribute("roles");
        
        QueryWrapper<TbCourseBooking> queryWrapper = new QueryWrapper<>();
        if (roles.contains("coach")) {
            queryWrapper.eq("coach_user_id", userId);
        }
        queryWrapper.orderByDesc("create_time");
        return Result.success(tbCourseBookingService.list(queryWrapper));
    }
    

    @Operation(summary = "查询教练可用时间槽")
    @GetMapping("/available-slots")
    @RequireRole({"user", "vip"})
    public Result<List<Map<String, Object>>> getAvailableSlots(@RequestParam Long coachId) {
        return Result.success(tbCourseBookingService.getAvailableSlots(coachId));
    }
    
    @Operation(summary = "取消预约")
    @PostMapping("/cancel")
    @RequireRole({"admin", "coach","vip"})
    public Result<Boolean> cancelBooking(@RequestParam Long bookingId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        // 验证预约是否属于当前用户
        TbCourseBooking booking = tbCourseBookingService.getById(bookingId);
        if (booking == null) {
            return Result.error("预约记录不存在");
        }
        
        if (!booking.getUserId().equals(userId)) {
            return Result.error("只能取消自己的预约");
        }
        
        Boolean result = tbCourseBookingService.cancelBooking(bookingId);
        return Result.success(result);
    }
}