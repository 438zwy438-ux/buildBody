package com.cdp.zwy.buildbody.module.system.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * 系统订单表(SysOrder)表实体类
 *
 * @author makejava
 * @since 2026-02-21 13:05:14
 */
@SuppressWarnings("serial")
public class SysOrder extends Model<SysOrder> {
//订单ID
    private Long id;
//订单编号(唯一)
    private String orderNo;
//下单用户ID
    private Long userId;
//订单标题
    private String subject;
//剩余次数（2026年2月20日22:50:06）
    private Integer remainCount;
//订单总金额
    private Double totalAmount;
//支付方式（1微信 2支付宝 3余额）
    private Integer payType;
//状态（0待支付 1已支付 2已取消 3已退款）
    private Integer status;
//支付时间
    private Date payTime;
//下单时间
    private Date createTime;
//订单类型（1会员卡2私教课）
    private Integer type;
//总次数
    private Integer totalCount;
//关联课程ID（私教课订单使用）
    private Long courseId;
//关联会员卡ID（会员卡订单使用）
    private Long cardId;


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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Integer getRemainCount() {
        return remainCount;
    }

    public void setRemainCount(Integer remainCount) {
        this.remainCount = remainCount;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
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