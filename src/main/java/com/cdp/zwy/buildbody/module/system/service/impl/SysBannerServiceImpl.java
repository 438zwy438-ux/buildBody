package com.cdp.zwy.buildbody.module.system.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cdp.zwy.buildbody.module.system.dao.SysBannerDao;
import com.cdp.zwy.buildbody.module.system.entity.SysBanner;
import com.cdp.zwy.buildbody.module.system.service.SysBannerService;
import org.springframework.stereotype.Service;

/**
 * 首页轮播图(SysBanner)表服务实现类
 *
 * @author makejava
 * @since 2026-02-16 09:31:32
 */
@Service("sysBannerService")
public class SysBannerServiceImpl extends ServiceImpl<SysBannerDao, SysBanner> implements SysBannerService {

}

