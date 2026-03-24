package com.cdp.zwy.buildbody.module.business.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.annotation.RequireRole;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.business.controller.DTO.MemberCardVO;
import com.cdp.zwy.buildbody.module.business.entity.TbMemberCard;
import com.cdp.zwy.buildbody.module.business.service.TbMemberCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

@RestController
@Tag(name = "会员持卡相关接口")
@RequestMapping("/tbMemberCard")
public class TbMemberCardController {
    @Resource
    private TbMemberCardService tbMemberCardService;

    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    @RequireRole("admin")
    public Result<Page<TbMemberCard>> selectAll(Page<TbMemberCard> page, TbMemberCard tbMemberCard) {
        return Result.success(this.tbMemberCardService.page(page, new QueryWrapper<>(tbMemberCard)));
    }

    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    @RequireRole("admin")
    public Result<TbMemberCard> selectOne(@PathVariable Serializable id) {
        return Result.success(this.tbMemberCardService.getById(id));
    }

    @Operation(summary = "查询我的会员卡")
    @GetMapping("/my-cards")
    @RequireRole({"user", "vip"})
    public Result<List<MemberCardVO>> getMyCards(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(this.tbMemberCardService.getMyCardsWithDetails(userId));
    }

    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    @RequireRole("admin")
    public Result<Boolean> insert(@RequestBody TbMemberCard tbMemberCard) {
        return Result.success(this.tbMemberCardService.save(tbMemberCard));
    }

    @Operation(summary = "修改数据")
    @PutMapping("/update")
    @RequireRole("admin")
    public Result<Boolean> update(@RequestBody TbMemberCard tbMemberCard) {
        return Result.success(this.tbMemberCardService.updateById(tbMemberCard));
    }

    @Operation(summary = "删除数据")
    @DeleteMapping("/delete")
    @RequireRole("admin")
    public Result<Boolean> delete(@RequestParam("idList") List<Long> idList) {
        return Result.success(this.tbMemberCardService.removeByIds(idList));
    }
}