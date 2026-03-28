package com.cdp.zwy.buildbody.module.business.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.annotation.RequireRole;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.business.entity.TbLocker;
import com.cdp.zwy.buildbody.module.business.service.TbLockerService;
import com.cdp.zwy.buildbody.module.system.entity.SysUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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

    @Operation(summary = "管理员临时开柜")
    @PutMapping("/adminUnlock/{id}")
    @RequireRole("admin")
    public Result<Boolean> adminUnlock(@PathVariable Long id) {
        return Result.success(this.tbLockerService.unlockLocker(id));
    }

    @Operation(summary = "管理员还柜")
    @PostMapping("/adminRelease/{id}")
    @RequireRole("admin")
    public Result<Boolean> adminRelease(@PathVariable Long id) {
        return Result.success(this.tbLockerService.adminRelease(id));
    }

    @Operation(summary = "验证会员身份(通过手机号)")
    @RequireRole({"user", "vip"})
    @PostMapping("/verify")
    public Result<Map<String, Object>> verifyMemberByPhone(@RequestBody VerifyMemberDTO dto, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("userId");
        Map<String, Object> user = tbLockerService.verifyMemberByPhone(dto.getPhone(), currentUserId);
        return Result.success(user);
    }

    @Operation(summary = "获取可用储物柜")
    @GetMapping("/available")
    public Result<List<TbLocker>> getAvailableLockers(@RequestParam String areaCode) {
        List<TbLocker> lockers = tbLockerService.getAvailableLockers(areaCode);
        return Result.success(lockers);
    }

    @Operation(summary = "获取我的储物柜")
    @GetMapping("/myLocker")
    @RequireRole({"user", "vip"})
    public Result<TbLocker> getMyLocker(@RequestParam String areaCode, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        TbLocker locker = tbLockerService.getMyLocker(userId, areaCode);
        return Result.success(locker);
    }

    @Operation(summary = "上锁储物柜")
    @PostMapping("/lock")
    @RequireRole({"user", "vip"})
    public Result<Boolean> lock(@RequestBody LockerIdDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(tbLockerService.lockByUserId(userId, dto.getLockerId()));
    }

    @Operation(summary = "使用储物柜")
    @PostMapping("/use")
    @RequireRole({"user", "vip"})
    public Result<Boolean> useLocker(@RequestBody LockerIdDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(tbLockerService.useLocker(userId, dto.getLockerId()));
    }

    @Operation(summary = "临时开柜")
    @PostMapping("/tempOpen")
    @RequireRole({"user", "vip"})
    public Result<Boolean> tempOpenLocker(@RequestBody LockerIdDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(tbLockerService.tempOpenByUserId(userId, dto.getLockerId()));
    }

    @Operation(summary = "还柜")
    @PostMapping("/return")
    @RequireRole({"user", "vip"})
    public Result<Boolean> returnLocker(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(tbLockerService.releaseLocker(userId));
    }

    public static class VerifyMemberDTO {
        private String phone;
        private String areaCode;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAreaCode() {
            return areaCode;
        }

        public void setAreaCode(String areaCode) {
            this.areaCode = areaCode;
        }
    }

    public static class LockerIdDTO {
        private Long lockerId;

        public Long getLockerId() {
            return lockerId;
        }

        public void setLockerId(Long lockerId) {
            this.lockerId = lockerId;
        }
    }
}