package com.cdp.zwy.buildbody.module.business.entity;


import java.util.Date;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * 会员持卡表(TbMemberCard)表实体类
 *
 * @author makejava
 * @since 2026-02-16 09:53:26
 */
@SuppressWarnings("serial")
public class TbMemberCard extends Model<TbMemberCard> {
//持卡记录ID
    private Long id;
//会员ID
    private Long userId;
//卡号(唯一)
    private String cardNo;
//关联卡种ID
    private Long templateId;
//总次数(次卡)
    private Integer totalCount;
//剩余次数(次卡)
    private Integer remainCount;
//激活/开卡时间
    private Date activeTime;
//过期时间
    private Date expireTime;
//状态（1正常 0已过期 2冻结）
    private Integer status;
//购买时间
    private Date createTime;


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

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getRemainCount() {
        return remainCount;
    }

    public void setRemainCount(Integer remainCount) {
        this.remainCount = remainCount;
    }

    public Date getActiveTime() {
        return activeTime;
    }

    public void setActiveTime(Date activeTime) {
        this.activeTime = activeTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
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

