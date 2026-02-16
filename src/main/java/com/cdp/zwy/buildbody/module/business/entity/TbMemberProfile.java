package com.cdp.zwy.buildbody.module.business.entity;


import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * 会员档案表(TbMemberProfile)表实体类
 *
 * @author makejava
 * @since 2026-02-16 09:53:44
 */
@SuppressWarnings("serial")
public class TbMemberProfile extends Model<TbMemberProfile> {
//档案ID
    private Long id;
//关联系统用户ID
    private Long userId;
//真实姓名
    private String realName;
//性别（0男 1女 2未知）
    private Integer gender;
//年龄
    private Integer age;
//人脸识别底库图片URL
    private String faceImgUrl;
//账户余额
    private Double balance;
//积分
    private Integer points;
//VIP状态（0普通 1VIP）
    private Integer isVip;
//VIP过期时间(若有)
    private Date vipExpireTime;


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

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getFaceImgUrl() {
        return faceImgUrl;
    }

    public void setFaceImgUrl(String faceImgUrl) {
        this.faceImgUrl = faceImgUrl;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getIsVip() {
        return isVip;
    }

    public void setIsVip(Integer isVip) {
        this.isVip = isVip;
    }

    public Date getVipExpireTime() {
        return vipExpireTime;
    }

    public void setVipExpireTime(Date vipExpireTime) {
        this.vipExpireTime = vipExpireTime;
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

