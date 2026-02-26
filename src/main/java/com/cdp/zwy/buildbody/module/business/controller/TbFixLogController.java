package com.cdp.zwy.buildbody.module.business.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.business.entity.TbFixLog;
import com.cdp.zwy.buildbody.module.business.service.TbFixLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 维修记录表(TbFixLog)表控制层
 *
 * @author makejava
 * @since 2026-02-26 23:15:11
 */
@RestController
@Tag(name = "维修记录相关接口")
@RequestMapping("/tbFixLog")
public class TbFixLogController {
    /**
     * 服务对象
     */
    @Resource
    private TbFixLogService tbFixLogService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param tbFixLog 查询实体
     * @return 所有数据
     */
    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    public Result<Page<TbFixLog>> selectAll(Page<TbFixLog> page, TbFixLog tbFixLog) {
        return Result.success(this.tbFixLogService.page(page, new QueryWrapper<>(tbFixLog)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    public Result<TbFixLog> selectOne(@PathVariable Serializable id) {
        return Result.success(this.tbFixLogService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param tbFixLog 实体对象
     * @return 新增结果
     */
    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    public Result<Boolean> insert(@RequestBody TbFixLog tbFixLog) {
        return Result.success(this.tbFixLogService.save(tbFixLog));
    }

    /**
     * 修改数据
     *
     * @param tbFixLog 实体对象
     * @return 修改结果
     */
    @Operation(summary = "修改数据")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody TbFixLog tbFixLog) {
        return Result.success(this.tbFixLogService.updateById(tbFixLog));
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
        return Result.success(this.tbFixLogService.removeByIds(idList));
    }
}