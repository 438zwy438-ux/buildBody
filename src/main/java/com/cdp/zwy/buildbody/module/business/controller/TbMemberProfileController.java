package com.cdp.zwy.buildbody.module.business.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.business.entity.TbMemberProfile;
import com.cdp.zwy.buildbody.module.business.service.TbMemberProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 会员档案表(TbMemberProfile)表控制层
 *
 * @author makejava
 * @since 2026-02-16 09:53:44
 */
@RestController
@Tag(name = "会员档案相关接口")
@RequestMapping("/tbMemberProfile")
public class TbMemberProfileController {
    /**
     * 服务对象
     */
    @Resource
    private TbMemberProfileService tbMemberProfileService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param tbMemberProfile 查询实体
     * @return 所有数据
     */
    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    public Result<Page<TbMemberProfile>> selectAll(Page<TbMemberProfile> page, TbMemberProfile tbMemberProfile) {
        return Result.success(this.tbMemberProfileService.page(page, new QueryWrapper<>(tbMemberProfile)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    public Result<TbMemberProfile> selectOne(@PathVariable Serializable id) {
        return Result.success(this.tbMemberProfileService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param tbMemberProfile 实体对象
     * @return 新增结果
     */
    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    public Result<Boolean> insert(@RequestBody TbMemberProfile tbMemberProfile) {
        return Result.success(this.tbMemberProfileService.save(tbMemberProfile));
    }

    /**
     * 修改数据
     *
     * @param tbMemberProfile 实体对象
     * @return 修改结果
     */
    @Operation(summary = "修改数据")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody TbMemberProfile tbMemberProfile) {
        return Result.success(this.tbMemberProfileService.updateById(tbMemberProfile));
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
        return Result.success(this.tbMemberProfileService.removeByIds(idList));
    }
}