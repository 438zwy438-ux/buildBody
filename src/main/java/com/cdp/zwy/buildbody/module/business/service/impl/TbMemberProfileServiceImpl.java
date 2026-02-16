package com.cdp.zwy.buildbody.module.business.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cdp.zwy.buildbody.module.business.dao.TbMemberProfileDao;
import com.cdp.zwy.buildbody.module.business.entity.TbMemberProfile;
import com.cdp.zwy.buildbody.module.business.service.TbMemberProfileService;
import org.springframework.stereotype.Service;

/**
 * 会员档案表(TbMemberProfile)表服务实现类
 *
 * @author makejava
 * @since 2026-02-16 09:53:44
 */
@Service("tbMemberProfileService")
public class TbMemberProfileServiceImpl extends ServiceImpl<TbMemberProfileDao, TbMemberProfile> implements TbMemberProfileService {

}

