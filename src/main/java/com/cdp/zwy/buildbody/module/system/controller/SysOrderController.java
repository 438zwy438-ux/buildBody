package com.cdp.zwy.buildbody.module.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.system.entity.SysOrder;
import com.cdp.zwy.buildbody.module.system.service.SysOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 系统订单表(SysOrder)表控制层
 *
 * @author makejava
 * @since 2026-02-21 13:05:11
 */
@RestController
@RequestMapping("/sysOrder")
@Tag(name = "系统订单相关接口")
public class SysOrderController {
    /**
     * 服务对象
     */
    @Resource
    private SysOrderService sysOrderService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param sysOrder 查询实体
     * @return 所有数据
     */
    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    public Result<Page<SysOrder>> selectAll(Page<SysOrder> page, SysOrder sysOrder) {
        return Result.success(this.sysOrderService.page(page, new QueryWrapper<>(sysOrder)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    public Result<SysOrder> selectOne(@PathVariable Serializable id) {
        return Result.success(this.sysOrderService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param sysOrder 实体对象
     * @return 新增结果
     */
    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    public Result<Boolean> insert(@RequestBody SysOrder sysOrder) {
        return Result.success(this.sysOrderService.save(sysOrder));
    }

    /**
     * 修改数据
     *
     * @param sysOrder 实体对象
     * @return 修改结果
     */
    @Operation(summary = "修改数据")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody SysOrder sysOrder) {
        return Result.success(this.sysOrderService.updateById(sysOrder));
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
        return Result.success(this.sysOrderService.removeByIds(idList));
    }
}