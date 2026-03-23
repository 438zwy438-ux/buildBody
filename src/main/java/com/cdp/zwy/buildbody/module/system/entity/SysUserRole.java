package com.cdp.zwy.buildbody.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * 用户和角色关联表(SysUserRole)表实体类
 *
 * @author makejava
 * @since 2026-02-17 10:22:38
 */
@SuppressWarnings("serial")
@TableName("sys_user_role")
public class SysUserRole extends Model<SysUserRole> {
    @TableId
    private Long id;
    private Long userId;
    private Long roleId;
    private String roleCode;


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

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    @Override
    public Serializable pkVal() {
        return this.id;
    }
}