package com.cdp.zwy.buildbody.module.business.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cdp.zwy.buildbody.module.business.dao.TbLockerDao;
import com.cdp.zwy.buildbody.module.business.entity.TbLocker;
import com.cdp.zwy.buildbody.module.business.service.TbLockerService;
import org.springframework.stereotype.Service;

/**
 * 更衣室储物柜表(TbLocker)表服务实现类
 *
 * @author makejava
 * @since 2026-02-16 09:53:06
 */
@Service("tbLockerService")
public class TbLockerServiceImpl extends ServiceImpl<TbLockerDao, TbLocker> implements TbLockerService {

}

