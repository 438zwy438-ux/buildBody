package com.cdp.zwy.buildbody.module.business.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.annotation.RequireRole;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.business.entity.TbMemberProfile;
import com.cdp.zwy.buildbody.module.business.service.TbMemberProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

@RestController
@Tag(name = "会员档案相关接口")
@RequestMapping("/tbMemberProfile")
public class TbMemberProfileController {
    @Resource
    private TbMemberProfileService tbMemberProfileService;

    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    @RequireRole("admin")
    public Result<Page<TbMemberProfile>> selectAll(Page<TbMemberProfile> page, TbMemberProfile tbMemberProfile) {
        return Result.success(this.tbMemberProfileService.page(page, new QueryWrapper<>(tbMemberProfile)));
    }

    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    @RequireRole("admin")
    public Result<TbMemberProfile> selectOne(@PathVariable Serializable id) {
        return Result.success(this.tbMemberProfileService.getById(id));
    }

    @Operation(summary = "查询我的会员信息")
    @GetMapping("/myProfile")
   
    public Result<TbMemberProfile> getMyProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        TbMemberProfile profile = tbMemberProfileService.getOne(new QueryWrapper<TbMemberProfile>().eq("user_id", userId));
        return Result.success(profile);
    }

    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    @RequireRole("admin")
    public Result<Boolean> insert(@RequestBody TbMemberProfile tbMemberProfile) {
        return Result.success(this.tbMemberProfileService.save(tbMemberProfile));
    }

    @Operation(summary = "修改数据")
    @PutMapping("/update")
    @RequireRole({"admin", "user", "vip"})
    public Result<Boolean> update(@RequestBody TbMemberProfile tbMemberProfile, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        
        if (!"admin".equals(role) && !tbMemberProfile.getUserId().equals(userId)) {
            return Result.error("只能修改自己的信息");
        }
        return Result.success(this.tbMemberProfileService.updateById(tbMemberProfile));
    }

    @Operation(summary = "删除数据")
    @DeleteMapping("/delete")
    @RequireRole("admin")
    public Result<Boolean> delete(@RequestParam("idList") List<Long> idList) {
        return Result.success(this.tbMemberProfileService.removeByIds(idList));
    }
}