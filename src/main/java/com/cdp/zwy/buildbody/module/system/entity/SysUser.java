package com.cdp.zwy.buildbody.module.system.entity;

import java.util.Date;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 系统用户表(SysUser)表实体类
 *
 * @author makejava
 * @since 2026-02-15 09:14:42
 */
@SuppressWarnings("serial")
@TableName("sys_user")
public class SysUser implements Serializable {
//主键ID
    @TableId
    private Long userId;
//登录账号
    private String username;
//加密密码(BCrypt)
    private String password;
//用户昵称
    private String nickname;
//手机号
    private String phone;
//头像地址
    private String avatar;
//帐号状态（1正常 0停用）
    private Integer status;
//删除标志（0代表存在 2代表删除）
    private Integer delFlag;
//创建时间
    private Date createTime;
//更新时间
    private Date updateTime;


    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}