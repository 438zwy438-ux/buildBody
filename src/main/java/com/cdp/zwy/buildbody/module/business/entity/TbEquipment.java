package com.cdp.zwy.buildbody.module.business.entity;


import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * 健身器材表(TbEquipment)表实体类
 *
 * @author makejava
 * @since 2026-02-16 09:52:35
 */
@SuppressWarnings("serial")
public class TbEquipment extends Model<TbEquipment> {
//器材ID
    private Long id;
//器材名称
    private String name;
//资产编号
    private String code;
//摆放位置
    private String location;
//购买日期
    private Date buyDate;
//状态（1正常 2维修 3报废）
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(Date buyDate) {
        this.buyDate = buyDate;
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

