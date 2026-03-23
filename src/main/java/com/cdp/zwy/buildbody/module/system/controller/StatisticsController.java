package com.cdp.zwy.buildbody.module.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cdp.zwy.buildbody.common.annotation.RequireRole;
import com.cdp.zwy.buildbody.common.result.Result;
import com.cdp.zwy.buildbody.module.business.dao.TbMemberCardDao;
import com.cdp.zwy.buildbody.module.business.dao.TbMemberProfileDao;
import com.cdp.zwy.buildbody.module.system.dao.SysOrderDao;
import com.cdp.zwy.buildbody.module.system.dao.TbEntryLogDao;
import com.cdp.zwy.buildbody.module.system.entity.SysOrder;
import com.cdp.zwy.buildbody.module.system.entity.TbEntryLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
@Tag(name = "统计数据接口")
public class StatisticsController {

    @Resource
    private TbMemberProfileDao memberProfileDao;

    @Resource
    private TbEntryLogDao entryLogDao;

    @Resource
    private SysOrderDao orderDao;

    @Resource
    private TbMemberCardDao memberCardDao;

    @Operation(summary = "获取Dashboard统计数据")
    @GetMapping("/dashboard")
    @RequireRole("ADMIN")
    public Result<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        Long memberCount = memberProfileDao.selectCount(null);
        stats.put("memberCount", memberCount);

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        Long todayEntryCount = entryLogDao.selectCount(new QueryWrapper<TbEntryLog>()
                .between("entry_time", todayStart, todayEnd));
        stats.put("todayEntryCount", todayEntryCount);

        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
        LocalDateTime monthEnd = LocalDateTime.of(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()), LocalTime.MAX);
        List<SysOrder> monthOrders = orderDao.selectList(new QueryWrapper<SysOrder>()
                .between("create_time", monthStart, monthEnd)
                .eq("status", 1));
        Double monthIncome = 0.0;
        for (SysOrder order : monthOrders) {
            if (order.getTotalAmount() != null) {
                monthIncome += order.getTotalAmount();
            }
        }
        stats.put("monthIncome", monthIncome);

        Long activeCardCount = memberCardDao.selectCount(new QueryWrapper<com.cdp.zwy.buildbody.module.business.entity.TbMemberCard>()
                .eq("status", 1)
                .gt("expire_time", LocalDateTime.now()));
        stats.put("activeCardCount", activeCardCount);

        return Result.success(stats);
    }

    @Operation(summary = "获取最近入场记录")
    @GetMapping("/recent-entries")
    @RequireRole("ADMIN")
    public Result<List<Map<String, Object>>> getRecentEntries() {
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        List<TbEntryLog> entries = entryLogDao.selectList(new QueryWrapper<TbEntryLog>()
                .between("entry_time", todayStart, todayEnd)
                .orderByDesc("entry_time")
                .last("LIMIT 10"));
        
        List<Map<String, Object>> result = entries.stream().map(entry -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", entry.getId());
            map.put("userId", entry.getUserId());
            map.put("userName", entry.getUserName());
            map.put("phone", entry.getPhone());
            map.put("entryTime", entry.getEntryTime());
            map.put("exitTime", entry.getExitTime());
            map.put("status", entry.getStatus());
            return map;
        }).toList();
        
        return Result.success(result);
    }
}