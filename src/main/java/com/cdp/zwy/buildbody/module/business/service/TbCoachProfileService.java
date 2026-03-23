package com.cdp.zwy.buildbody.module.business.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.cdp.zwy.buildbody.module.business.entity.TbCoachProfile;

import java.util.List;

public interface TbCoachProfileService extends IService<TbCoachProfile> {
    List<String> getCoachImages(Long coachId);
}