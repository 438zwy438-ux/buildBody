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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Tag(name = "教练档案相关接口")
@RequestMapping("/tbCoachProfile")
public class TbCoachProfileController {
    @Resource
    private TbCoachProfileService tbCoachProfileService;

    @Operation(summary = "通过用户ID获取教练档案")
    @GetMapping("/by-user-id/{userId}")
    @RequireRole(requireLogin = true)
    public Result<TbCoachProfile> getByUserId(@PathVariable Long userId) {
        TbCoachProfile coachProfile = this.tbCoachProfileService.getByUserId(userId);
        if (coachProfile == null) {
            return Result.error("未找到教练档案");
        }
        return Result.success(coachProfile);
    }

    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    @RequireRole(requireLogin = false)
    public Result<Page<Map<String, Object>>> selectAll(Page<TbCoachProfile> page, TbCoachProfile tbCoachProfile) {
        Page<TbCoachProfile> coachPage = this.tbCoachProfileService.page(page, new QueryWrapper<>(tbCoachProfile));
        
        Page<Map<String, Object>> resultPage = new Page<>(coachPage.getCurrent(), coachPage.getSize(), coachPage.getTotal());
        List<Map<String, Object>> records = coachPage.getRecords().stream().map(coach -> {
            Map<String, Object> record = new HashMap<>();
            record.put("id", coach.getId());
            record.put("userId", coach.getUserId());
            record.put("realName", coach.getRealName());
            record.put("specialty", coach.getSpecialty());
            record.put("intro", coach.getIntro());
            record.put("certificates", coach.getCertificates());
            record.put("entryDate", coach.getEntryDate());
            record.put("status", coach.getStatus());
            record.put("images", this.tbCoachProfileService.getCoachImages(coach.getId()));
            return record;
        }).collect(Collectors.toList());
        
        resultPage.setRecords(records);
        return Result.success(resultPage);
    }

    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    @RequireRole(requireLogin = false)
    public Result<TbCoachProfile> selectOne(@PathVariable Serializable id) {
        return Result.success(this.tbCoachProfileService.getById(id));
    }

    @Operation(summary = "获取教练详情（包含图片）")
    @GetMapping("/detail/{id}")
    @RequireRole(requireLogin = false)
    public Result<Map<String, Object>> getDetail(@PathVariable Serializable id) {
        TbCoachProfile coach = this.tbCoachProfileService.getById(id);
        Long coachId = Long.valueOf(id.toString());
        List<String> images = this.tbCoachProfileService.getCoachImages(coachId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("coach", coach);
        result.put("images", images);
        
        return Result.success(result);
    }

    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    @RequireRole("admin")
    public Result<Boolean> insert(@RequestBody TbCoachProfile tbCoachProfile) {
        return Result.success(this.tbCoachProfileService.save(tbCoachProfile));
    }

    @Operation(summary = "修改数据")
    @PutMapping("/update")
    @RequireRole({"admin", "coach"})
    public Result<Boolean> update(@RequestBody TbCoachProfile tbCoachProfile) {
        return Result.success(this.tbCoachProfileService.updateById(tbCoachProfile));
    }

    @Operation(summary = "删除数据")
    @DeleteMapping("/delete")
    @RequireRole("admin")
    public Result<Boolean> delete(@RequestParam("idList") List<Long> idList) {
        return Result.success(this.tbCoachProfileService.removeByIds(idList));
    }
}