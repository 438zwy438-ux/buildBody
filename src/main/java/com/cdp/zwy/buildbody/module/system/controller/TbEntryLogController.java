package com.cdp.zwy.buildbody.module.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.system.controller.DTO.CheckInSearchDTO;
import com.cdp.zwy.buildbody.module.system.controller.VO.MemberCheckVO;
import com.cdp.zwy.buildbody.module.system.entity.TbEntryLog;
import com.cdp.zwy.buildbody.module.system.service.TbEntryLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 进出场记录表(TbEntryLog)表控制层
 *
 * @author makejava
 * @since 2026-02-19 09:46:33
 */
@RestController
@Tag(name = "进出场记录相关接口")
@RequestMapping("/tbEntryLog")
public class TbEntryLogController {
    /**
     * 服务对象
     */
    @Resource
    private TbEntryLogService tbEntryLogService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param tbEntryLog 查询实体
     * @return 所有数据
     */
    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    public Result<Page<TbEntryLog>> selectAll(Page<TbEntryLog> page, TbEntryLog tbEntryLog) {
        return Result.success(this.tbEntryLogService.page(page, new QueryWrapper<>(tbEntryLog)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    public Result<TbEntryLog> selectOne(@PathVariable Serializable id) {
        return Result.success(this.tbEntryLogService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param tbEntryLog 实体对象
     * @return 新增结果
     */
    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    public Result<Boolean> insert(@RequestBody TbEntryLog tbEntryLog) {
        return Result.success(this.tbEntryLogService.save(tbEntryLog));
    }

    /**
     * 修改数据
     *
     * @param tbEntryLog 实体对象
     * @return 修改结果
     */
    @Operation(summary = "修改数据")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody TbEntryLog tbEntryLog) {
        return Result.success(this.tbEntryLogService.updateById(tbEntryLog));
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
        return Result.success(this.tbEntryLogService.removeByIds(idList));
    }

    /**
     * 业务接口
     * @param dto
     * @return
     */
    @Operation(summary = "1. 搜索会员(核验入场资格)")
    @PostMapping("/search")
    public Result<List<MemberCheckVO>> search(@RequestBody CheckInSearchDTO dto) {
        return Result.success(tbEntryLogService.searchMemberForCheckIn(dto.getPhone()));
    }

    @Operation(summary = "2. 确认入场(管理员操作)")
    @PostMapping("/checkIn")
    public Result<Boolean> checkIn(@RequestParam Long userId) {
        // 这里 adminId 暂时写死或者从 Token 获取，毕设演示写 1L (Admin) 即可
        return Result.success(tbEntryLogService.confirmCheckIn(userId, 1L));
    }

    @Operation(summary = "3. 确认出场")
    @PostMapping("/checkOut")
    public Result<Boolean> checkOut(@RequestParam Long userId) {
        return Result.success(tbEntryLogService.confirmCheckOut(userId));
    }
}