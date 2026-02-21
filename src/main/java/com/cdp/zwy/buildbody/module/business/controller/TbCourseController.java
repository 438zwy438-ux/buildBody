package com.cdp.zwy.buildbody.module.business.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.business.controller.DTO.CourseAddDTO;
import com.cdp.zwy.buildbody.module.business.controller.DTO.CoursePurchaseDTO;
import com.cdp.zwy.buildbody.module.business.entity.TbCourse;
import com.cdp.zwy.buildbody.module.business.entity.TbMemberProfile;
import com.cdp.zwy.buildbody.module.business.service.TbCourseService;
import com.cdp.zwy.buildbody.module.business.service.TbMemberProfileService;
import com.cdp.zwy.buildbody.module.system.service.SysOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * 课程信息表(TbCourse)表控制层
 *
 * @author makejava
 * @since 2026-02-16 09:48:13
 */
@RestController
@Tag(name = "课程信息相关接口")
@RequestMapping("/tbCourse")
public class TbCourseController {
    /**
     * 服务对象
     */
    @Resource
    private TbCourseService tbCourseService;
    
    @Resource
    private SysOrderService sysOrderService;
    
    @Resource
    private TbMemberProfileService memberProfileService;

    /**
     * 分页查询所有数据
     *
     * @param page 分页对象
     * @param tbCourse 查询实体
     * @return 所有数据
     */
    @Operation(summary = "分页查询所有数据")
    @GetMapping("/selectAll")
    public Result<Page<TbCourse>> selectAll(Page<TbCourse> page, TbCourse tbCourse) {
        return Result.success(this.tbCourseService.page(page, new QueryWrapper<>(tbCourse)));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @Operation(summary = "通过主键查询单条数据")
    @GetMapping("/{id}")
    public Result<TbCourse> selectOne(@PathVariable Serializable id) {
        return Result.success(this.tbCourseService.getById(id));
    }

    /**
     * 新增数据
     *
     * @param tbCourse 实体对象
     * @return 新增结果
     */
    @Operation(summary = "新增数据")
    @PostMapping("/insert")
    public Result<Boolean> insert(@RequestBody TbCourse tbCourse) {
        return Result.success(this.tbCourseService.save(tbCourse));
    }

    /**
     * 修改数据
     *
     * @param tbCourse 实体对象
     * @return 修改结果
     */
    @Operation(summary = "修改数据")
    @PutMapping("/update")
    public Result<Boolean> update(@RequestBody TbCourse tbCourse) {
        return Result.success(this.tbCourseService.updateById(tbCourse));
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
        return Result.success(this.tbCourseService.removeByIds(idList));
    }

    /**
     * 添加私教课
     */
    @Operation(summary = "添加私教课")
    @PostMapping("/addPrivate")
    public Result<Boolean> addPrivate(@RequestBody CourseAddDTO dto) {
        return Result.success(tbCourseService.addPrivateCourse(dto));
    }
    
    /**
     * 购买课程
     */
    @Operation(summary = "购买课程")
    @PostMapping("/purchase")
    public Result<Long> purchaseCourse(@RequestBody CoursePurchaseDTO dto) {
        // 根据课程ID获取课程信息
        TbCourse course = tbCourseService.getById(dto.getCourseId());
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }
        
        // 计算总课程次数：产品包含的课程次数 × 购买数量
        Integer totalCourseTimes = course.getCourseTimes() * dto.getQuantity();
        
        // 计算总金额：课程单价 × 购买数量（使用BigDecimal保持精度）
        java.math.BigDecimal totalAmount = course.getPrice().multiply(new java.math.BigDecimal(dto.getQuantity()));
        
        // 创建课程订单
        Long orderId = sysOrderService.createCourseOrder(dto.getUserId(), totalCourseTimes, totalAmount.doubleValue());
        // 自动支付订单（实际项目中应该有支付流程）
        sysOrderService.payOrder(orderId);
        
        // 更新会员VIP状态为1（购买私教课后成为VIP）
        TbMemberProfile memberProfile = memberProfileService.getOne(new QueryWrapper<TbMemberProfile>().eq("user_id", dto.getUserId()));
        if (memberProfile != null) {
            memberProfile.setIsVip(1);
            memberProfileService.updateById(memberProfile);
        }
        
        return Result.success(orderId);
    }
}