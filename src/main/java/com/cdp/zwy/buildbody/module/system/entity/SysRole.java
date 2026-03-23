package com.cdp.zwy.buildbody.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import java.util.Date;

/**
 * 角色表(SysRole)表实体类
 *
 * @author generator
 * @since 2026-02-17 10:22:38
 */
@SuppressWarnings("serial")
@TableName("sys_role")
public class SysRole extends Model<SysRole> {
    @TableId
    private Long roleId;
    private String roleName;
    private String roleKey; // 对应数据库中的role_key字段
    private Integer roleSort;
    private Integer status;
    private Date createTime;


    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    public Integer getRoleSort() {
        return roleSort;
    }

    public void setRoleSort(Integer roleSort) {
        this.roleSort = roleSort;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public Serializable pkVal() {
        return this.roleId;
    }
}