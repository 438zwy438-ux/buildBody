package com.cdp.zwy.buildbody.module.business.entity;

import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * 教练档案表(TbCoachProfile)表实体类
 *
 * @author makejava
 * @since 2026-02-16 09:49:04
 */
@SuppressWarnings("serial")
public class TbCoachProfile extends Model<TbCoachProfile> {
//档案ID
    private Long id;
//关联系统用户ID
    private Long userId;
//真实姓名
    private String realName;
//特长标签(逗号分隔)
    private String specialty;
//个人简介(富文本)
    private String intro;
//证书图片列表(JSON数组)
    private String certificates;
//入职日期
    private Date entryDate;
//状态（1在职 0离职）
    private Integer status;


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

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getCertificates() {
        return certificates;
    }

    public void setCertificates(String certificates) {
        this.certificates = certificates;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
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

