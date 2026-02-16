package com.cdp.zwy.buildbody.module.business.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.cdp.zwy.buildbody.module.business.dao.TbMemberCardDao;
import com.cdp.zwy.buildbody.module.business.entity.TbMemberCard;
import com.cdp.zwy.buildbody.module.business.service.TbMemberCardService;
import org.springframework.stereotype.Service;

/**
 * 会员持卡表(TbMemberCard)表服务实现类
 *
 * @author makejava
 * @since 2026-02-16 09:53:26
 */
@Service("tbMemberCardService")
public class TbMemberCardServiceImpl extends ServiceImpl<TbMemberCardDao, TbMemberCard> implements TbMemberCardService {

}

