package com.cdp.zwy.buildbody.module.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.system.entity.SysUserRole;
import com.cdp.zwy.buildbody.module.system.service.SysUserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 用户和角色关联表(SysUserRole)表控制层
 *
 * @author makejava
 * @since 2026-02-17 10:22:38
 */
@RestController
@Tag(name = "用户角色关联相关接口")
@RequestMapping("/sysUserRole")
public class SysUserRoleController {
    /**
     * 服务对象
     */
    @Resource
    private SysUserRoleService sysUserRoleService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param sysUserRole 查询实体
     * @return 所有数据
     */
    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    public Result<Page<SysUserRole>> selectAll(Page<SysUserRole> page, SysUserRole sysUserRole) {
        return Result.success(this.sysUserRoleService.page(page, new QueryWrapper<>(sysUserRole)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    public Result<SysUserRole> selectOne(@PathVariable Serializable id) {
        return Result.success(this.sysUserRoleService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param sysUserRole 实体对象
     * @return 新增结果
     */
    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    public Result<Boolean> insert(@RequestBody SysUserRole sysUserRole) {
        return Result.success(this.sysUserRoleService.save(sysUserRole));
    }

    /**
     * 修改数据
     *
     * @param sysUserRole 实体对象
     * @return 修改结果
     */
    @Operation(summary = "修改数据")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody SysUserRole sysUserRole) {
        return Result.success(this.sysUserRoleService.updateById(sysUserRole));
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
        return Result.success(this.sysUserRoleService.removeByIds(idList));
    }
}