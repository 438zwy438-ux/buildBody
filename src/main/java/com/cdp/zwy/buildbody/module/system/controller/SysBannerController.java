package com.cdp.zwy.buildbody.module.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.system.entity.SysBanner;
import com.cdp.zwy.buildbody.module.system.service.SysBannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 首页轮播图(SysBanner)表控制层
 *
 * @author makejava
 * @since 2026-02-16 09:31:32
 */
@RestController
@RequestMapping("/sysBanner")
@Tag(name="首页轮播图相关接口")
public class SysBannerController {
    /**
     * 服务对象
     */
    @Resource
    private SysBannerService sysBannerService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param sysBanner 查询实体
     * @return 所有数据
     */
    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    public Result<Page<SysBanner>> selectAll(Page<SysBanner> page, SysBanner sysBanner) {
        return Result.success(this.sysBannerService.page(page, new QueryWrapper<>(sysBanner)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    public Result<SysBanner> selectOne(@PathVariable Serializable id) {
        return Result.success(this.sysBannerService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param sysBanner 实体对象
     * @return 新增结果
     */
    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    public Result<Boolean> insert(@RequestBody SysBanner sysBanner) {
        return Result.success(this.sysBannerService.save(sysBanner));
    }

    /**
     * 修改数据
     *
     * @param sysBanner 实体对象
     * @return 修改结果
     */
    @Operation(summary = "修改数据")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody SysBanner sysBanner) {
        return Result.success(this.sysBannerService.updateById(sysBanner));
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
        return Result.success(this.sysBannerService.removeByIds(idList));
    }
}