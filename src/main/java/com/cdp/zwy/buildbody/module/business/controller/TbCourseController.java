package com.cdp.zwy.buildbody.module.business.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.annotation.RequireRole;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.business.controller.DTO.CourseAddDTO;
import com.cdp.zwy.buildbody.module.business.controller.DTO.CoursePurchaseDTO;
import com.cdp.zwy.buildbody.module.business.entity.TbCourse;
import com.cdp.zwy.buildbody.module.business.entity.TbMemberProfile;
import com.cdp.zwy.buildbody.module.business.service.TbCourseService;
import com.cdp.zwy.buildbody.module.business.service.TbMemberProfileService;
import com.cdp.zwy.buildbody.module.system.entity.SysOrder;
import com.cdp.zwy.buildbody.module.system.service.SysOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Tag(name = "课程信息相关接口")
@RequestMapping("/tbCourse")
public class TbCourseController {
    @Resource
    private TbCourseService tbCourseService;
    
    @Resource
    private SysOrderService sysOrderService;
    
    @Resource
    private TbMemberProfileService memberProfileService;

    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    @RequireRole(requireLogin = false,value = {"user", "vip"})
    public Result<Page<TbCourse>> selectAll(Page<TbCourse> page, TbCourse tbCourse) {
        return Result.success(this.tbCourseService.page(page, new QueryWrapper<>(tbCourse)));
    }

    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    @RequireRole(requireLogin = false,value = {"user", "vip"})
    public Result<TbCourse> selectOne(@PathVariable Serializable id) {
        return Result.success(this.tbCourseService.getById(id));
    }

    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    @RequireRole("admin")
    public Result<Boolean> insert(@RequestBody TbCourse tbCourse) {
        return Result.success(this.tbCourseService.save(tbCourse));
    }

    @Operation(summary = "修改数据")
    @PutMapping("/update")
    @RequireRole({"admin", "coach"})
    public Result<Boolean> update(@RequestBody TbCourse tbCourse) {
        return Result.success(this.tbCourseService.updateById(tbCourse));
    }

    @Operation(summary = "删除数据")
    @DeleteMapping("/delete")
    @RequireRole("admin")
    public Result<Boolean> delete(@RequestParam("idList") List<Long> idList) {
        return Result.success(this.tbCourseService.removeByIds(idList));
    }

    @Operation(summary = "添加私教课")
    @PostMapping("/addPrivate")
    @RequireRole({"admin", "coach"})
    public Result<Boolean> addPrivate(@RequestBody CourseAddDTO dto) {
        return Result.success(tbCourseService.addPrivateCourse(dto));
    }
    
    @Operation(summary = "购买课程")
    @PostMapping("/purchase")
    @RequireRole({"user", "vip"})
    public Result<Long> purchaseCourse(@RequestBody CoursePurchaseDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        dto.setUserId(userId);
        
        TbCourse course = tbCourseService.getById(dto.getCourseId());
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        
        Integer totalCourseTimes = course.getCourseTimes() * dto.getQuantity();
        java.math.BigDecimal totalAmount = course.getPrice().multiply(new java.math.BigDecimal(dto.getQuantity()));
        
        Long orderId = sysOrderService.createCourseOrder(dto.getUserId(), dto.getCourseId(), totalCourseTimes, totalAmount.doubleValue());
        sysOrderService.payOrder(orderId);
        
        TbMemberProfile memberProfile = memberProfileService.getOne(new QueryWrapper<TbMemberProfile>().eq("user_id", dto.getUserId()));
        if (memberProfile != null) {
            memberProfile.setIsVip(1);
            memberProfileService.updateById(memberProfile);
        }
        
        return Result.success(orderId);
    }

    @Operation(summary = "查询用户的私教课程列表")
    @GetMapping("/my-courses")
    @RequireRole({"user", "vip"})
    public Result<List<TbCourse>> getMyPrivateCourses(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        QueryWrapper<SysOrder> orderQuery = new QueryWrapper<>();
        orderQuery.eq("user_id", userId);
        orderQuery.eq("type", 2);
        orderQuery.eq("status", 1);
        orderQuery.gt("remain_count", 0);
        
        List<SysOrder> orders = sysOrderService.list(orderQuery);
        if (orders.isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        
        Set<Long> courseIds = orders.stream()
                .map(SysOrder::getCourseId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        if (courseIds.isEmpty()) {
            QueryWrapper<TbCourse> courseQuery = new QueryWrapper<>();
            courseQuery.eq("type", 1);
            courseQuery.eq("status", 1);
            courseQuery.orderByDesc("create_time");
            return Result.success(tbCourseService.list(courseQuery));
        }
        
        QueryWrapper<TbCourse> courseQuery = new QueryWrapper<>();
        courseQuery.in("id", courseIds);
        courseQuery.eq("status", 1);
        courseQuery.orderByDesc("create_time");
        
        return Result.success(tbCourseService.list(courseQuery));
    }
    
    @Operation(summary = "查询用户的私教课订单")
    @GetMapping("/my-orders")
    @RequireRole({"user", "vip"})
    public Result<List<SysOrder>> getMyPrivateOrders(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        
        QueryWrapper<SysOrder> orderQuery = new QueryWrapper<>();
        orderQuery.eq("user_id", userId);
        orderQuery.eq("type", 2);
        orderQuery.eq("status", 1);
        orderQuery.orderByDesc("create_time");
        
        return Result.success(sysOrderService.list(orderQuery));
    }
}