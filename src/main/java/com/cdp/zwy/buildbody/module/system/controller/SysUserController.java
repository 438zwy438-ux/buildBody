package com.cdp.zwy.buildbody.module.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.annotation.RequireRole;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.system.controller.DTO.CoachAddDTO;
import com.cdp.zwy.buildbody.module.system.controller.DTO.CoachRegisterDTO;
import com.cdp.zwy.buildbody.module.system.controller.DTO.LoginDTO;
import com.cdp.zwy.buildbody.module.system.controller.DTO.RegisterDTO;
import com.cdp.zwy.buildbody.module.system.controller.VO.LoginVO;
import com.cdp.zwy.buildbody.module.system.entity.SysUser;
import com.cdp.zwy.buildbody.module.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

@RestController
@Tag(name = "账号相关接口")
@RequestMapping("/sysUser")
public class SysUserController {
    @Resource
    private SysUserService sysUserService;

    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")

    public Result<Page<SysUser>> selectAll(Page<SysUser> page, SysUser sysUser) {
        return Result.success(this.sysUserService.page(page, new QueryWrapper<>(sysUser)));
    }

    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    @RequireRole({"admin", "coach"})
    public Result<SysUser> selectOne(@PathVariable Serializable id) {
        return Result.success(this.sysUserService.getById(id));
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    @RequireRole({"user", "vip", "coach", "admin"})
    public Result<SysUser> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(this.sysUserService.getById(userId));
    }

    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    @RequireRole("admin")
    public Result<Boolean> insert(@RequestBody SysUser sysUser) {
        return Result.success(this.sysUserService.save(sysUser));
    }

    @Operation(summary = "修改数据")
    @PutMapping("/update")
    @RequireRole({"admin", "user", "vip", "coach"})
    public Result<Boolean> update(@RequestBody SysUser sysUser, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        
        // 管理员可以更新任意用户，其他角色只能更新自己
        if (!"admin".equals(role) && !sysUser.getUserId().equals(userId)) {
            return Result.error("只能更新自己的信息");
        }
        return Result.success(this.sysUserService.updateById(sysUser));
    }

    @Operation(summary = "删除数据")
    @DeleteMapping("/delete")
    @RequireRole("admin")
    public Result<Boolean> delete(@RequestParam("idList") List<Long> idList) {
        return Result.success(this.sysUserService.removeByIds(idList));
    }

    @Operation(summary = "统一登录接口(支持管理员/会员/教练)")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        if (loginDTO.getUsername() == null || loginDTO.getPassword() == null) {
            return Result.error("账号或密码不能为空");
        }
        return Result.success(sysUserService.login(loginDTO));
    }

    @Operation(summary = "访客注册并办卡")
    @PostMapping("/register")
    public Result<Boolean> register(@RequestBody RegisterDTO dto) {
        if (dto.getCardTemplateId() == null) {
            return Result.error("必须选择一种会员卡！");
        }
        return Result.success(sysUserService.registerMember(dto));
    }

    @Operation(summary = "添加教练")
    @PostMapping("/addCoach")
    @RequireRole("admin")
    public Result<Boolean> addCoach(@RequestBody CoachAddDTO dto) {
        return Result.success(sysUserService.addCoach(dto));
    }

    @Operation(summary = "教练注册")
    @PostMapping("/registerCoach")
    public Result<Boolean> registerCoach(@RequestBody CoachRegisterDTO dto) {
        return Result.success(sysUserService.registerCoach(dto));
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    @RequireRole({"user", "vip", "coach", "admin"})
    public Result<Boolean> logout(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        sysUserService.logout(userId);
        return Result.success(true);
    }
}