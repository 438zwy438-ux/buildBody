package com.cdp.zwy.buildbody.module.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cdp.zwy.buildbody.module.system.dao.SysOrderDao;
import com.cdp.zwy.buildbody.module.system.entity.SysOrder;
import com.cdp.zwy.buildbody.module.system.service.SysOrderService;
import org.springframework.stereotype.Service;

/**
 * 系统订单表(SysOrder)表服务实现类
 *
 * @author makejava
 * @since 2026-02-21 13:05:15
 */
@Service("sysOrderService")
public class SysOrderServiceImpl extends ServiceImpl<SysOrderDao, SysOrder> implements SysOrderService {

}

