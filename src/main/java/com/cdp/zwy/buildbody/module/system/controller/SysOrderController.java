package com.cdp.zwy.buildbody.module.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.annotation.RequireRole;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.system.controller.VO.CourseOrderVO;
import com.cdp.zwy.buildbody.module.system.entity.SysOrder;
import com.cdp.zwy.buildbody.module.system.service.SysOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

@RestController
@RequestMapping("/sysOrder")
@Tag(name = "系统订单相关接口")
public class SysOrderController {
    @Resource
    private SysOrderService sysOrderService;

    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    @RequireRole("admin")
    public Result<Page<SysOrder>> selectAll(Page<SysOrder> page, SysOrder sysOrder) {
        return Result.success(this.sysOrderService.page(page, new QueryWrapper<>(sysOrder)));
    }

    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    @RequireRole("admin")
    public Result<SysOrder> selectOne(@PathVariable Serializable id) {
        return Result.success(this.sysOrderService.getById(id));
    }

    @Operation(summary = "查询我的订单")
    @GetMapping("/my-orders")
    @RequireRole({"user", "vip"})
    public Result<List<SysOrder>> getMyOrders(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        QueryWrapper<SysOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("create_time");
        return Result.success(this.sysOrderService.list(queryWrapper));
    }
    @Operation(summary = "查询我的私教课订单")
    @GetMapping("/my-courses-orders")
    @RequireRole({ "user", "vip"})
//    考虑到VIP会降级为普通会员所以加上user角色
    public Result<List<CourseOrderVO>> getMyCoursesOrders(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<CourseOrderVO> courseOrders = sysOrderService.getMyCourseOrders(userId);
        return Result.success(courseOrders);
    }

    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    @RequireRole("admin")
    public Result<Boolean> insert(@RequestBody SysOrder sysOrder) {
        return Result.success(this.sysOrderService.save(sysOrder));
    }

    @Operation(summary = "修改数据")
    @PutMapping("/update")
    @RequireRole("admin")
    public Result<Boolean> update(@RequestBody SysOrder sysOrder) {
        return Result.success(this.sysOrderService.updateById(sysOrder));
    }

    @Operation(summary = "删除数据")
    @DeleteMapping("/delete")
    @RequireRole("admin")
    public Result<Boolean> delete(@RequestParam("idList") List<Long> idList) {
        return Result.success(this.sysOrderService.removeByIds(idList));
    }

    @Operation(summary = "取消订单")
    @PostMapping("/cancel")
    @RequireRole({"user", "vip"})
    public Result<Boolean> cancelOrder(@RequestParam Long orderId) {
        return Result.success(this.sysOrderService.cancelOrder(orderId));
    }

    @Operation(summary = "退款订单")
    @PostMapping("/refund")
    @RequireRole({"user", "vip"})
    public Result<Boolean> refundOrder(@RequestParam Long orderId) {
        return Result.success(this.sysOrderService.refundOrder(orderId));
    }
}