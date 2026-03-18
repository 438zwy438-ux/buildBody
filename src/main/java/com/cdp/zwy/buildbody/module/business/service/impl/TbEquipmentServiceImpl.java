package com.cdp.zwy.buildbody.module.business.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cdp.zwy.buildbody.module.business.dao.TbEquipmentDao;
import com.cdp.zwy.buildbody.module.business.entity.TbEquipment;
import com.cdp.zwy.buildbody.module.business.service.TbEquipmentService;
import com.cdp.zwy.buildbody.module.system.entity.ImgRelation;
import com.cdp.zwy.buildbody.module.system.service.ImgRelationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("tbEquipmentService")
public class TbEquipmentServiceImpl extends ServiceImpl<TbEquipmentDao, TbEquipment> implements TbEquipmentService {

    @Resource
    private ImgRelationService imgRelationService;

    @Override
    public List<String> getEquipmentImages(Long equipmentId) {
        List<ImgRelation> imgRelations = imgRelationService.list(
            new QueryWrapper<ImgRelation>()
                .eq("relation_type", 1)
                .eq("relation_id", equipmentId)
                .orderByAsc("create_time")
        );
        return imgRelations.stream()
            .map(ImgRelation::getImgUrl)
            .collect(Collectors.toList());
    }
}