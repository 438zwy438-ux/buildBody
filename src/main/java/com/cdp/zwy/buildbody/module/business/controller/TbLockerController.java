package com.cdp.zwy.buildbody.module.business.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.business.entity.TbLocker;
import com.cdp.zwy.buildbody.module.business.service.TbLockerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
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
    /**
     * 服务对象
     */
    @Resource
    private TbLockerService tbLockerService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param tbLocker 查询实体
     * @return 所有数据
     */
    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    public Result<Page<TbLocker>> selectAll(Page<TbLocker> page, TbLocker tbLocker) {
        return Result.success(this.tbLockerService.page(page, new QueryWrapper<>(tbLocker)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    public Result<TbLocker> selectOne(@PathVariable Serializable id) {
        return Result.success(this.tbLockerService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param tbLocker 实体对象
     * @return 新增结果
     */
    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    public Result<Boolean> insert(@RequestBody TbLocker tbLocker) {
        return Result.success(this.tbLockerService.save(tbLocker));
    }

    /**
     * 修改数据
     *
     * @param tbLocker 实体对象
     * @return 修改结果
     */
    @Operation(summary = "修改数据")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody TbLocker tbLocker) {
        return Result.success(this.tbLockerService.updateById(tbLocker));
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
        return Result.success(this.tbLockerService.removeByIds(idList));
    }
}