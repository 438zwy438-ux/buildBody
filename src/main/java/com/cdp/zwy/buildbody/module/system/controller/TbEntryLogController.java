package com.cdp.zwy.buildbody.module.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.annotation.RequireRole;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.system.controller.DTO.CheckInSearchDTO;
import com.cdp.zwy.buildbody.module.system.controller.VO.MemberCheckVO;
import com.cdp.zwy.buildbody.module.system.entity.TbEntryLog;
import com.cdp.zwy.buildbody.module.system.service.TbEntryLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

@RestController
@Tag(name = "进出场记录相关接口")
@RequestMapping("/tbEntryLog")
public class TbEntryLogController {
    @Resource
    private TbEntryLogService tbEntryLogService;

    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    @RequireRole("admin")
    public Result<Page<TbEntryLog>> selectAll(Page<TbEntryLog> page, TbEntryLog tbEntryLog) {
        return Result.success(this.tbEntryLogService.page(page, new QueryWrapper<>(tbEntryLog)));
    }

    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    @RequireRole("admin")
    public Result<TbEntryLog> selectOne(@PathVariable Serializable id) {
        return Result.success(this.tbEntryLogService.getById(id));
    }

    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    @RequireRole("admin")
    public Result<Boolean> insert(@RequestBody TbEntryLog tbEntryLog) {
        return Result.success(this.tbEntryLogService.save(tbEntryLog));
    }

    @Operation(summary = "修改数据")
    @PutMapping("/update")
    @RequireRole("admin")
    public Result<Boolean> update(@RequestBody TbEntryLog tbEntryLog) {
        return Result.success(this.tbEntryLogService.updateById(tbEntryLog));
    }

    @Operation(summary = "删除数据")
    @DeleteMapping("/delete")
    @RequireRole("admin")
    public Result<Boolean> delete(@RequestParam("idList") List<Long> idList) {
        return Result.success(this.tbEntryLogService.removeByIds(idList));
    }

    @Operation(summary = "搜索会员(核验入场资格)")
    @PostMapping("/search")
    @RequireRole("admin")
    public Result<List<MemberCheckVO>> search(@RequestBody CheckInSearchDTO dto) {
        return Result.success(tbEntryLogService.searchMemberForCheckIn(dto.getPhone()));
    }

    @Operation(summary = "确认入场(管理员操作)")
    @PostMapping("/checkIn")
    @RequireRole("admin")
    public Result<Boolean> checkIn(@RequestParam Long userId) {
        return Result.success(tbEntryLogService.confirmCheckIn(userId, 1L));
    }

    @Operation(summary = "确认出场")
    @PostMapping("/checkOut")
    public Result<Boolean> checkOut(@RequestParam Long id) {
        return Result.success(tbEntryLogService.confirmCheckOut(id));
    }

    @Operation(summary = "查询我的出勤记录")
    @GetMapping("/my-logs")
    @RequireRole({"user", "vip"})
    public Result<List<TbEntryLog>> getMyLogs(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        QueryWrapper<TbEntryLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("entry_time");
        return Result.success(tbEntryLogService.list(queryWrapper));
    }
}