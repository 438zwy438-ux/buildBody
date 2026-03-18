package com.cdp.zwy.buildbody.module.business.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.cdp.zwy.buildbody.module.business.entity.TbEquipment;

import java.util.List;

public interface TbEquipmentService extends IService<TbEquipment> {
    List<String> getEquipmentImages(Long equipmentId);
}