package com.cdp.zwy.buildbody.module.system.entity;


import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;
import java.util.Date;

/**
 * 图片关系表(ImgRelation)表实体类
 *
 * @author makejava
 * @since 2026-03-18 11:18:52
 */
@SuppressWarnings("serial")
public class ImgRelation extends Model<ImgRelation> {
//图片关联记录ID（主键）
    private Integer id;
//关联类型
    private Integer relationType;
//关联表ID（对应关联类型表的主键）
    private Integer relationId;
//图片地址（URL）
    private String imgUrl;
//记录创建时间
    private Date createTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRelationType() {
        return relationType;
    }

    public void setRelationType(Integer relationType) {
        this.relationType = relationType;
    }

    public Integer getRelationId() {
        return relationId;
    }

    public void setRelationId(Integer relationId) {
        this.relationId = relationId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
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

