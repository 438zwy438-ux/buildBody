package com.cdp.zwy.buildbody.module.business.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.annotation.RequireRole;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.business.entity.TbEquipment;
import com.cdp.zwy.buildbody.module.business.service.TbEquipmentService;
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
@Tag(name = "健身器材相关接口")
@RequestMapping("/tbEquipment")
public class TbEquipmentController {
    @Resource
    private TbEquipmentService tbEquipmentService;

    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    @RequireRole(requireLogin = false)
    public Result<Page<Map<String, Object>>> selectAll(Page<TbEquipment> page, TbEquipment tbEquipment) {
        Page<TbEquipment> equipmentPage = this.tbEquipmentService.page(page, new QueryWrapper<>(tbEquipment));
        
        Page<Map<String, Object>> resultPage = new Page<>(equipmentPage.getCurrent(), equipmentPage.getSize(), equipmentPage.getTotal());
        List<Map<String, Object>> records = equipmentPage.getRecords().stream().map(equipment -> {
            Map<String, Object> record = new HashMap<>();
            record.put("id", equipment.getId());
            record.put("name", equipment.getName());
            record.put("code", equipment.getCode());
            record.put("location", equipment.getLocation());
            record.put("buyDate", equipment.getBuyDate());
            record.put("status", equipment.getStatus());
            record.put("detailDesc", equipment.getDetailDesc());
            record.put("images", this.tbEquipmentService.getEquipmentImages(equipment.getId()));
            return record;
        }).collect(java.util.stream.Collectors.toList());
        
        resultPage.setRecords(records);
        return Result.success(resultPage);
    }

    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    @RequireRole(requireLogin = false)
    public Result<TbEquipment> selectOne(@PathVariable Serializable id) {
        return Result.success(this.tbEquipmentService.getById(id));
    }

    @Operation(summary = "获取设备详情（包含图片）")
    @GetMapping("/detail/{id}")
    @RequireRole(requireLogin = false)
    public Result<Map<String, Object>> getDetail(@PathVariable Serializable id) {
        TbEquipment equipment = this.tbEquipmentService.getById(id);
        Long equipmentId = Long.valueOf(id.toString());
        List<String> images = this.tbEquipmentService.getEquipmentImages(equipmentId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("equipment", equipment);
        result.put("images", images);
        
        return Result.success(result);
    }

    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    @RequireRole("ADMIN")
    public Result<Map<String, Object>> insert(@RequestBody TbEquipment tbEquipment) {
        boolean success = this.tbEquipmentService.save(tbEquipment);
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("equipmentId", tbEquipment.getId());
        return Result.success(result);
    }

    @Operation(summary = "修改数据")
    @PutMapping("/update")
    @RequireRole("ADMIN")
    public Result<Boolean> update(@RequestBody TbEquipment tbEquipment) {
        return Result.success(this.tbEquipmentService.updateById(tbEquipment));
    }

    @Operation(summary = "删除数据")
    @DeleteMapping("/delete")
    @RequireRole("ADMIN")
    public Result<Boolean> delete(@RequestParam("idList") List<Long> idList) {
        return Result.success(this.tbEquipmentService.removeByIds(idList));
    }
}