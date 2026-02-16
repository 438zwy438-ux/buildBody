package com.cdp.zwy.buildbody.module.business.entity;


import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.io.Serializable;

/**
 * 更衣室储物柜表(TbLocker)表实体类
 *
 * @author makejava
 * @since 2026-02-16 09:53:06
 */
@SuppressWarnings("serial")
public class TbLocker extends Model<TbLocker> {
//储物柜ID
    private Long id;
//区域编号(A区)
    private String areaCode;
//柜号
    private Integer boxNo;
//当前使用者ID(空闲为NULL)
    private Long currentUserId;
//状态（0空闲 1占用 2故障）
    private Integer status;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public Integer getBoxNo() {
        return boxNo;
    }

    public void setBoxNo(Integer boxNo) {
        this.boxNo = boxNo;
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
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

