package com.cdp.zwy.buildbody.module.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.system.entity.ImgRelation;
import com.cdp.zwy.buildbody.module.system.service.ImgRelationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 图片关系表(ImgRelation)表控制层
 *
 * @author makejava
 * @since 2026-03-18 11:18:52
 */
@RestController
@RequestMapping("/imgRelation")
@Tag(name="图片关系表相关接口")
public class ImgRelationController {
    /**
     * 服务对象
     */
    @Resource
    private ImgRelationService imgRelationService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param imgRelation 查询实体
     * @return 所有数据
     */
    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    public Result<Page<ImgRelation>> selectAll(Page<ImgRelation> page, ImgRelation imgRelation) {
        return Result.success(this.imgRelationService.page(page, new QueryWrapper<>(imgRelation)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    public Result<ImgRelation> selectOne(@PathVariable Serializable id) {
        return Result.success(this.imgRelationService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param imgRelation 实体对象
     * @return 新增结果
     */
    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    public Result<Boolean> insert(@RequestBody ImgRelation imgRelation) {
        return Result.success(this.imgRelationService.save(imgRelation));
    }

    /**
     * 修改数据
     *
     * @param imgRelation 实体对象
     * @return 修改结果
     */
    @Operation(summary = "修改数据")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody ImgRelation imgRelation) {
        return Result.success(this.imgRelationService.updateById(imgRelation));
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
        return Result.success(this.imgRelationService.removeByIds(idList));
    }
}