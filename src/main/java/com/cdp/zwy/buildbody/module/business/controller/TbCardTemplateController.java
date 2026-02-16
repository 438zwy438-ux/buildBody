package com.cdp.zwy.buildbody.module.business.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.business.entity.TbCardTemplate;
import com.cdp.zwy.buildbody.module.business.service.TbCardTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 会员卡模板表(TbCardTemplate)表控制层
 *
 * @author makejava
 * @since 2026-02-16 09:46:39
 */
@RestController
@Tag(name = "会员卡模板相关接口")
@RequestMapping("/tbCardTemplate")
public class TbCardTemplateController {
    /**
     * 服务对象
     */
    @Resource
    private TbCardTemplateService tbCardTemplateService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param tbCardTemplate 查询实体
     * @return 所有数据
     */
    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    public Result<Page<TbCardTemplate>> selectAll(Page<TbCardTemplate> page, TbCardTemplate tbCardTemplate) {
        return Result.success(this.tbCardTemplateService.page(page, new QueryWrapper<>(tbCardTemplate)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    public Result<TbCardTemplate> selectOne(@PathVariable Serializable id) {
        return Result.success(this.tbCardTemplateService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param tbCardTemplate 实体对象
     * @return 新增结果
     */
    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    public Result<Boolean> insert(@RequestBody TbCardTemplate tbCardTemplate) {
        return Result.success(this.tbCardTemplateService.save(tbCardTemplate));
    }

    /**
     * 修改数据
     *
     * @param tbCardTemplate 实体对象
     * @return 修改结果
     */
    @Operation(summary = "修改数据")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody TbCardTemplate tbCardTemplate) {
        return Result.success(this.tbCardTemplateService.updateById(tbCardTemplate));
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
        return Result.success(this.tbCardTemplateService.removeByIds(idList));
    }
}