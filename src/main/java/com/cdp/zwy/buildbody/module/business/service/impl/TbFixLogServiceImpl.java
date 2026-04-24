package com.cdp.zwy.buildbody.module.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cdp.zwy.buildbody.module.business.dao.TbFixLogDao;
import com.cdp.zwy.buildbody.module.business.entity.TbFixLog;
import com.cdp.zwy.buildbody.module.business.entity.TbEquipment;
import com.cdp.zwy.buildbody.module.business.service.TbEquipmentService;
import com.cdp.zwy.buildbody.module.business.service.TbFixLogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 维修记录表(TbFixLog)表服务实现类
 *
 * @author makejava
 * @since 2026-02-26 23:15:11
 */
@Service("tbFixLogService")
public class TbFixLogServiceImpl extends ServiceImpl<TbFixLogDao, TbFixLog> implements TbFixLogService {

    @Resource
    private TbEquipmentService tbEquipmentService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveFixLogAndUpdateEquipmentStatus(TbFixLog tbFixLog) {
        boolean saveResult = this.save(tbFixLog);
        
        if (saveResult && tbFixLog.getEquipmentId() != null) {
            Long equipmentId = Long.parseLong(tbFixLog.getEquipmentId());
            TbEquipment equipment = tbEquipmentService.getById(equipmentId);
            if (equipment != null) {
                equipment.setStatus(2);
                tbEquipmentService.updateById(equipment);
            }
        }
        
        return saveResult;
    }
}