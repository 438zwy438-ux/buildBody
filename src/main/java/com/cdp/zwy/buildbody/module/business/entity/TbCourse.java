package com.cdp.zwy.buildbody.module.business.entity;


import java.math.BigDecimal;
import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * 课程信息表(TbCourse)表实体类
 *
 * @author makejava
 * @since 2026-02-16 09:48:13
 */
@SuppressWarnings("serial")
public class TbCourse extends Model<TbCourse> {
//课程ID
    private Long id;
//关联教练ID(空为公开课)
    private Long coachUserId;
//课程名称
    private String name;
//课程类型（1私教 2团课）
    private Integer type;
//课程单价
    private BigDecimal price;
//时长(分钟)
    private Integer duration;
//封面图URL
    private String coverImg;
//课程详情
    private String description;
//状态（1上架 0下架）
    private Integer status;
//创建时间
    private Date createTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCoachUserId() {
        return coachUserId;
    }

    public void setCoachUserId(Long coachUserId) {
        this.coachUserId = coachUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

