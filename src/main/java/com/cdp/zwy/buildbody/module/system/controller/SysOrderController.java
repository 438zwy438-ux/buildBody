package com.cdp.zwy.buildbody.module.system.controller;



import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.module.system.entity.SysOrder;
import com.cdp.zwy.buildbody.module.system.service.SysOrderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;

/**
 * 系统订单表(SysOrder)表控制层
 *
 * @author makejava
 * @since 2026-02-20 23:03:02
 */
@RestController
@RequestMapping("sysOrder")
public class SysOrderController extends ApiController {
    /**
     * 服务对象
     */
    @Resource
    private SysOrderService sysOrderService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param sysOrder 查询实体
     * @return 所有数据
     */
    @GetMapping
    public R selectAll(Page<SysOrder> page, SysOrder sysOrder) {
        return success(this.sysOrderService.page(page, new QueryWrapper<>(sysOrder)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public R selectOne(@PathVariable Serializable id) {
        return success(this.sysOrderService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param sysOrder 实体对象
     * @return 新增结果
     */
    @PostMapping
    public R insert(@RequestBody SysOrder sysOrder) {
        return success(this.sysOrderService.save(sysOrder));
    }

    /**
     * 修改数据
     *
     * @param sysOrder 实体对象
     * @return 修改结果
     */
    @PutMapping
    public R update(@RequestBody SysOrder sysOrder) {
        return success(this.sysOrderService.updateById(sysOrder));
    }

    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @DeleteMapping
    public R delete(@RequestParam("idList") List<Long> idList) {
        return success(this.sysOrderService.removeByIds(idList));
    }
}

