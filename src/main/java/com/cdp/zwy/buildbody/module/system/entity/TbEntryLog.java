package com.cdp.zwy.buildbody.module.system.entity;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * 进出场记录表(TbEntryLog)表实体类
 *
 * @author makejava
 * @since 2026-02-19 09:46:36
 */
@SuppressWarnings("serial")
public class TbEntryLog extends Model<TbEntryLog> {
//日志ID
    private Long id;
//入场人员ID
    private Long userId;
//人员类型（1会员 2教练 3员工）
    private Integer userType;
//入场时间
    private LocalDateTime entryTime;
//出场时间
    private LocalDateTime exitTime;
//验证方式（1人脸 2扫码 3人工）
    private Integer verifyMode;
//放行管理员ID(若人工)
    private Long adminId;
//体温
    private Float temperature;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public Integer getVerifyMode() {
        return verifyMode;
    }

    public void setVerifyMode(Integer verifyMode) {
        this.verifyMode = verifyMode;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    /**
     * 获取主键值
     *
     * @return 主键值
     */
    @Override
    public Serializable pkVal() {
        return this.id;
    }
}