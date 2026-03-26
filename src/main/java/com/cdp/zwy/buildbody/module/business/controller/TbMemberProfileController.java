package com.cdp.zwy.buildbody.module.business.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.annotation.RequireRole;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.business.controller.VO.MemberDetailVO;
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
    @RequireRole({"user", "vip"})
//    不加RequireRole注解，所有用户都可以访问，包括未登录用户和管理员用户，且不会解析token中的角色
    public Result<TbMemberProfile> getMyProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        TbMemberProfile profile = tbMemberProfileService.getOne(new QueryWrapper<TbMemberProfile>().eq("user_id", userId));
        return Result.success(profile);
    }

    @Operation(summary = "查询会员详情信息")
    @GetMapping("/detail/{userId}")
    @RequireRole("admin")
    public Result<MemberDetailVO> getMemberDetail(@PathVariable Long userId) {
        MemberDetailVO memberDetail = tbMemberProfileService.getMemberDetailByUserId(userId);
        if (memberDetail == null) {
            return Result.error("会员信息不存在");
        }
        return Result.success(memberDetail);
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
        
        // 如果传入了id，使用updateById方法
        if (tbMemberProfile.getId() != null) {
            return Result.success(this.tbMemberProfileService.updateById(tbMemberProfile));
        } 
        // 如果没有传入id但传入了userId，使用updateByUserId方法
        else if (tbMemberProfile.getUserId() != null) {
            return Result.success(this.tbMemberProfileService.updateByUserId(tbMemberProfile));
        }
        // 如果既没有id也没有userId，返回错误
        else {
            return Result.error("更新失败：缺少必要的标识信息");
        }
    }

    @Operation(summary = "删除数据")
    @DeleteMapping("/delete")
    @RequireRole("admin")
    public Result<Boolean> delete(@RequestParam("idList") List<Long> idList) {
        return Result.success(this.tbMemberProfileService.removeByIds(idList));
    }
}