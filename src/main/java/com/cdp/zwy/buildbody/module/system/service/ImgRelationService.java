package com.cdp.zwy.buildbody.module.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cdp.zwy.buildbody.module.system.entity.ImgRelation;

import java.util.List;

/**
 * 图片关系表(ImgRelation)表服务接口
 *
 * @author makejava
 * @since 2026-03-18 11:18:52
 */
public interface ImgRelationService extends IService<ImgRelation> {

    /**
     * 批量保存图片关系
     *
     * @param relationType 关联类型
     * @param relationId 关联ID
     * @param imgUrls 图片URL列表
     * @return 保存结果
     */
    Boolean batchSave(Integer relationType, Long relationId, List<String> imgUrls);

    /**
     * 删除指定关联类型的所有图片
     *
     * @param relationType 关联类型
     * @param relationId 关联ID
     * @return 删除结果
     */
    Boolean deleteByRelation(Integer relationType, Long relationId);
}