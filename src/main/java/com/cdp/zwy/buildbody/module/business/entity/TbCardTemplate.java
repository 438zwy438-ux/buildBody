package com.cdp.zwy.buildbody.module.business.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * 会员卡模板表(TbCardTemplate)表实体类
 *
 * @author makejava
 * @since 2026-02-16 09:46:48
 */
@SuppressWarnings("serial")
public class TbCardTemplate extends Model<TbCardTemplate> {
//卡种ID
    private Long id;
//卡名称(如:季卡)
    private String name;
//类型（1期限卡 2次卡）
    private Integer type;
//标准售价
    private Double price;
//有效期天数
    private Integer durationDays;
//包含次数(仅次卡有效)
    private Integer times;
//权益描述
    private String description;
//状态（1上架 0下架）
    private Integer status;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(Integer durationDays) {
        this.durationDays = durationDays;
    }

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
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

