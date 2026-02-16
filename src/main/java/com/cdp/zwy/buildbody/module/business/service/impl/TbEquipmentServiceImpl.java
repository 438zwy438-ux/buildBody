package com.cdp.zwy.buildbody.module.business.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cdp.zwy.buildbody.module.business.dao.TbEquipmentDao;
import com.cdp.zwy.buildbody.module.business.entity.TbEquipment;
import com.cdp.zwy.buildbody.module.business.service.TbEquipmentService;
import org.springframework.stereotype.Service;

/**
 * 健身器材表(TbEquipment)表服务实现类
 *
 * @author makejava
 * @since 2026-02-16 09:52:35
 */
@Service("tbEquipmentService")
public class TbEquipmentServiceImpl extends ServiceImpl<TbEquipmentDao, TbEquipment> implements TbEquipmentService {

}

