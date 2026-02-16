package com.cdp.zwy.buildbody.module.business.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * 课程预约记录表(TbCourseBooking)表实体类
 *
 * @author makejava
 * @since 2026-02-16 09:50:03
 */
@SuppressWarnings("serial")
public class TbCourseBooking extends Model<TbCourseBooking> {
//预约ID
    private Long id;
//关联支付订单ID
    private Long orderId;
//会员ID
    private Long userId;
//教练ID
    private Long coachUserId;
//课程ID
    private Long courseId;
//预约上课时间
    private Date scheduleTime;
//状态（0待核销 1已完成 2已取消）
    private Integer status;
//实际核销时间
    private Date checkTime;
//预约操作时间
    private Date createTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCoachUserId() {
        return coachUserId;
    }

    public void setCoachUserId(Long coachUserId) {
        this.coachUserId = coachUserId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Date getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(Date scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

