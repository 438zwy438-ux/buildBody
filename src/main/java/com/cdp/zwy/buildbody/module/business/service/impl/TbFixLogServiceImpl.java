package com.cdp.zwy.buildbody.module.business.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cdp.zwy.buildbody.module.business.dao.TbFixLogDao;
import com.cdp.zwy.buildbody.module.business.entity.TbFixLog;
import com.cdp.zwy.buildbody.module.business.service.TbFixLogService;
import org.springframework.stereotype.Service;

/**
 * 维修记录表(TbFixLog)表服务实现类
 *
 * @author makejava
 * @since 2026-02-26 23:15:11
 */
@Service("tbFixLogService")
public class TbFixLogServiceImpl extends ServiceImpl<TbFixLogDao, TbFixLog> implements TbFixLogService {

}

