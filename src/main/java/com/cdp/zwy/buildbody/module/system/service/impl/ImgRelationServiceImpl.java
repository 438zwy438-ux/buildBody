package com.cdp.zwy.buildbody.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cdp.zwy.buildbody.module.system.dao.ImgRelationDao;
import com.cdp.zwy.buildbody.module.system.entity.ImgRelation;
import com.cdp.zwy.buildbody.module.system.service.ImgRelationService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 图片关系表(ImgRelation)表服务实现类
 *
 * @author makejava
 * @since 2026-03-18 11:18:52
 */
@Service("imgRelationService")
public class ImgRelationServiceImpl extends ServiceImpl<ImgRelationDao, ImgRelation> implements ImgRelationService {

    @Override
    public Boolean batchSave(Integer relationType, Long relationId, List<String> imgUrls) {
        if (imgUrls == null || imgUrls.isEmpty()) {
            return true;
        }
        
        List<ImgRelation> relations = new ArrayList<>();
        Date now = new Date();
        
        for (String imgUrl : imgUrls) {
            ImgRelation relation = new ImgRelation();
            relation.setRelationType(relationType);
            relation.setRelationId(relationId);
            relation.setImgUrl(imgUrl);
            relation.setCreateTime(now);
            relations.add(relation);
        }
        
        return this.saveBatch(relations);
    }

    @Override
    public Boolean deleteByRelation(Integer relationType, Long relationId) {
        QueryWrapper<ImgRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("relation_type", relationType);
        queryWrapper.eq("relation_id", relationId);
        return this.remove(queryWrapper);
    }
}