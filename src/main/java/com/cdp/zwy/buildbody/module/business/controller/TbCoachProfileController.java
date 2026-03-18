package com.cdp.zwy.buildbody.module.business.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.annotation.RequireRole;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.business.entity.TbCoachProfile;
import com.cdp.zwy.buildbody.module.business.service.TbCoachProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

@RestController
@Tag(name = "教练档案相关接口")
@RequestMapping("/tbCoachProfile")
public class TbCoachProfileController {
    @Resource
    private TbCoachProfileService tbCoachProfileService;

    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    @RequireRole(requireLogin = false)
    public Result<Page<TbCoachProfile>> selectAll(Page<TbCoachProfile> page, TbCoachProfile tbCoachProfile) {
        return Result.success(this.tbCoachProfileService.page(page, new QueryWrapper<>(tbCoachProfile)));
    }

    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    @RequireRole(requireLogin = false)
    public Result<TbCoachProfile> selectOne(@PathVariable Serializable id) {
        return Result.success(this.tbCoachProfileService.getById(id));
    }

    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    @RequireRole("ADMIN")
    public Result<Boolean> insert(@RequestBody TbCoachProfile tbCoachProfile) {
        return Result.success(this.tbCoachProfileService.save(tbCoachProfile));
    }

    @Operation(summary = "修改数据")
    @PutMapping("/update")
    @RequireRole({"ADMIN", "COACH"})
    public Result<Boolean> update(@RequestBody TbCoachProfile tbCoachProfile) {
        return Result.success(this.tbCoachProfileService.updateById(tbCoachProfile));
    }

    @Operation(summary = "删除数据")
    @DeleteMapping("/delete")
    @RequireRole("ADMIN")
    public Result<Boolean> delete(@RequestParam("idList") List<Long> idList) {
        return Result.success(this.tbCoachProfileService.removeByIds(idList));
    }
}