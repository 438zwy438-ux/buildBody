package com.cdp.zwy.buildbody.module.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cdp.zwy.buildbody.module.business.dao.TbCoachProfileDao;
import com.cdp.zwy.buildbody.module.business.entity.TbCoachProfile;
import com.cdp.zwy.buildbody.module.business.service.TbCoachProfileService;
import com.cdp.zwy.buildbody.module.system.entity.ImgRelation;
import com.cdp.zwy.buildbody.module.system.service.ImgRelationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("tbCoachProfileService")
public class TbCoachProfileServiceImpl extends ServiceImpl<TbCoachProfileDao, TbCoachProfile> implements TbCoachProfileService {

    @Resource
    private ImgRelationService imgRelationService;

    @Override
    public List<String> getCoachImages(Long coachId) {
        List<ImgRelation> imgRelations = imgRelationService.list(
            new QueryWrapper<ImgRelation>()
                .eq("relation_type", 2)
                .eq("relation_id", coachId)
                .orderByAsc("create_time")
        );
        return imgRelations.stream()
            .map(ImgRelation::getImgUrl)
            .collect(Collectors.toList());
    }
}