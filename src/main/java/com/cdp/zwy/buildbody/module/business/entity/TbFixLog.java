package com.cdp.zwy.buildbody.module.business.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * 维修记录表(TbFixLog)表实体类
 *
 * @author makejava
 * @since 2026-02-26 23:15:11
 */
@SuppressWarnings("serial")
public class TbFixLog extends Model<TbFixLog> {
//维修记录ID（主键）
    private Integer id;
//维修器械ID
    private String equipmentId;
//损坏位置
    private String damagePosition;
//损坏说明
    private String damageDescription;
//损坏时间
    private Date damageTime;
//维修人员姓名
    private String repairerName;
//维修人员电话
    private String repairerPhone;
//维修时间（完成维修的时间）
    private Date repairTime;
//记录创建时间
    private Date createTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(String equipmentId) {
        this.equipmentId = equipmentId;
    }

    public String getDamagePosition() {
        return damagePosition;
    }

    public void setDamagePosition(String damagePosition) {
        this.damagePosition = damagePosition;
    }

    public String getDamageDescription() {
        return damageDescription;
    }

    public void setDamageDescription(String damageDescription) {
        this.damageDescription = damageDescription;
    }

    public Date getDamageTime() {
        return damageTime;
    }

    public void setDamageTime(Date damageTime) {
        this.damageTime = damageTime;
    }

    public String getRepairerName() {
        return repairerName;
    }

    public void setRepairerName(String repairerName) {
        this.repairerName = repairerName;
    }

    public String getRepairerPhone() {
        return repairerPhone;
    }

    public void setRepairerPhone(String repairerPhone) {
        this.repairerPhone = repairerPhone;
    }

    public Date getRepairTime() {
        return repairTime;
    }

    public void setRepairTime(Date repairTime) {
        this.repairTime = repairTime;
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

