package com.cdp.zwy.buildbody.module.business.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.annotation.RequireRole;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.business.entity.TbLocker;
import com.cdp.zwy.buildbody.module.business.service.TbLockerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 更衣室储物柜表(TbLocker)表控制层
 *
 * @author makejava
 * @since 2026-02-16 09:53:06
 */
@RestController
@Tag(name = "更衣室储物柜相关接口")
@RequestMapping("/tbLocker")
public class TbLockerController {
    @Resource
    private TbLockerService tbLockerService;

    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    public Result<Page<TbLocker>> selectAll(Page<TbLocker> page, TbLocker tbLocker) {
        return Result.success(this.tbLockerService.page(page, new QueryWrapper<>(tbLocker)));
    }

    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    public Result<TbLocker> selectOne(@PathVariable Serializable id) {
        return Result.success(this.tbLockerService.getById(id));
    }

    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    @RequireRole("admin")
    public Result<Boolean> insert(@RequestBody TbLocker tbLocker) {
        return Result.success(this.tbLockerService.save(tbLocker));
    }

    @Operation(summary = "修改数据")
    @PutMapping("/update")
    @RequireRole("admin")
    public Result<Boolean> update(@RequestBody TbLocker tbLocker) {
        return Result.success(this.tbLockerService.updateById(tbLocker));
    }

    @Operation(summary = "删除数据")
    @DeleteMapping("/delete")
    @RequireRole("admin")
    public Result<Boolean> delete(@RequestParam("idList") List<Long> idList) {
        return Result.success(this.tbLockerService.removeByIds(idList));
    }
    
    @Operation(summary = "上锁储物柜")
    @PutMapping("/lock/{id}")
    @RequireRole({"user", "vip"})
    public Result<Boolean> lock(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        TbLocker locker = tbLockerService.getById(id);
        if (locker == null || !locker.getCurrentUserId().equals(userId)) {
            return Result.error("只能操作自己使用的储物柜");
        }
        return Result.success(this.tbLockerService.lockLocker(id));
    }
    
    @Operation(summary = "解锁储物柜")
    @PutMapping("/unlock/{id}")
    @RequireRole({"user", "vip"})
    public Result<Boolean> unlock(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        TbLocker locker = tbLockerService.getById(id);
        if (locker == null || !locker.getCurrentUserId().equals(userId)) {
            return Result.error("只能操作自己使用的储物柜");
        }
        return Result.success(this.tbLockerService.unlockLocker(id));
    }

    @Operation(summary = "使用储物柜")
    @PostMapping("/use/{lockerId}")
    @RequireRole({"user", "vip"})
    public Result<Boolean> useLocker(@PathVariable Long lockerId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(this.tbLockerService.useLocker(userId, lockerId));
    }

    @Operation(summary = "释放储物柜")
    @PostMapping("/release")
    @RequireRole({"user", "vip"})
    public Result<Boolean> releaseLocker(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(this.tbLockerService.releaseLocker(userId));
    }

    @Operation(summary = "查询我的储物柜")
    @GetMapping("/myLocker")
    @RequireRole({"user", "vip"})
    public Result<TbLocker> getMyLocker(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        TbLocker locker = tbLockerService.getOne(new QueryWrapper<TbLocker>().eq("current_user_id", userId));
        return Result.success(locker);
    }
}