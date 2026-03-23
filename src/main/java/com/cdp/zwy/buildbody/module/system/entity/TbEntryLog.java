package com.cdp.zwy.buildbody.module.system.entity;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * 进出场记录表(TbEntryLog)表实体类
 *
 * @author makejava
 * @since 2026-02-19 09:46:36
 */
@SuppressWarnings("serial")
@TableName("tb_entry_log")
public class TbEntryLog extends Model<TbEntryLog> {
    private Long id;
    private Long userId;
    private String userName;
    private String phone;
    private Integer userType;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private String status;
    private Integer verifyMode;
    private Long adminId;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}