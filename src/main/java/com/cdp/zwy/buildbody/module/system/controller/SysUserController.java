package com.cdp.zwy.buildbody.module.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.system.controller.DTO.CoachAddDTO;
import com.cdp.zwy.buildbody.module.system.controller.DTO.LoginDTO;
import com.cdp.zwy.buildbody.module.system.controller.DTO.RegisterDTO;
import com.cdp.zwy.buildbody.module.system.controller.VO.LoginVO;
import com.cdp.zwy.buildbody.module.system.entity.SysUser;
import com.cdp.zwy.buildbody.module.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 系统用户表(SysUser)表控制层
 *
 * @author makejava
 * @since 2026-02-15 09:14:42
 */
@RestController
@Tag(name = "账号相关接口")
@RequestMapping("/sysUser")
public class SysUserController {
    /**
     * 服务对象
     */
    @Resource
    private SysUserService sysUserService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param sysUser 查询实体
     * @return 所有数据
     */
    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    public Result<Page<SysUser>> selectAll(Page<SysUser> page, SysUser sysUser) {
        return Result.success(this.sysUserService.page(page, new QueryWrapper<>(sysUser)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    public Result<SysUser> selectOne(@PathVariable Serializable id) {
        return Result.success(this.sysUserService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param sysUser 实体对象
     * @return 新增结果
     */
    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    public Result<Boolean> insert(@RequestBody SysUser sysUser) {
        return Result.success(this.sysUserService.save(sysUser));
    }

    /**
     * 修改数据
     *
     * @param sysUser 实体对象
     * @return 修改结果
     */
    @Operation(summary = "修改数据")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody SysUser sysUser) {
        return Result.success(this.sysUserService.updateById(sysUser));
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
        return Result.success(this.sysUserService.removeByIds(idList));
    }

    /**
     * 统一登录接口
     */
    @Operation(summary = "统一登录接口(支持管理员/会员/教练)")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        // 简单的判空
        if (loginDTO.getUsername() == null || loginDTO.getPassword() == null) {
            return Result.error("账号或密码不能为空");
        }
        return Result.success(sysUserService.login(loginDTO));
    }
    /**
     * 访客注册并办卡
     */
    @Operation(summary = "访客注册并办卡")
    @PostMapping("/register")
    public Result<Boolean> register(@RequestBody RegisterDTO dto) {
        // 简单参数校验
        if (dto.getCardTemplateId() == null) {
            return Result.error("必须选择一种会员卡！");
        }
        return Result.success(sysUserService.registerMember(dto));
    }
    /**
     * 添加教练
     */
    @Operation(summary = "添加教练")
    @PostMapping("/addCoach")
    public Result<Boolean> addCoach(@RequestBody CoachAddDTO dto) {
        return Result.success(sysUserService.addCoach(dto));
    }
}
