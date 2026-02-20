package com.cdp.zwy.buildbody.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * 系统订单表(SysOrder)表实体类
 *
 * @author makejava
 * @since 2026-02-20 10:00:00
 */
@SuppressWarnings("serial")
public class SysOrder extends Model<SysOrder> {
//订单ID
    private Long id;
//订单编号
    private String orderNo;
//用户ID
    private Long userId;
//订单类型（1会员卡 2课程预约）
    private Integer type;
//订单金额
    private BigDecimal amount;
//支付状态（0待支付 1已支付 2已退款）
    private Integer payStatus;
//支付时间
    private Date payTime;
//剩余次数
    @TableField("remain_count")
    private Integer remainCount;
//创建时间
    private Date createTime;
//更新时间
    private Date updateTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Integer getRemainCount() {
        return remainCount;
    }

    public void setRemainCount(Integer remainCount) {
        this.remainCount = remainCount;
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