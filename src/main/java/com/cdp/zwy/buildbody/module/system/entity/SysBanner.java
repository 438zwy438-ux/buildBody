package com.cdp.zwy.buildbody.module.system.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * 首页轮播图(SysBanner)表实体类
 *
 * @author makejava
 * @since 2026-02-16 09:31:32
 */
@SuppressWarnings("serial")
public class SysBanner extends Model<SysBanner> {
//ID
    private Long id;
//图片地址
    private String imgUrl;
//跳转链接
    private String linkUrl;
//排序
    private Integer sort;
//状态（1启用 0禁用）
    private Integer status;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
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

